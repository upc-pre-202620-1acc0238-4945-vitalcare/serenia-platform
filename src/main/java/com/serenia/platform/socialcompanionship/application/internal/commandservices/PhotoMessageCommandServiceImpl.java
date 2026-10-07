package com.serenia.platform.socialcompanionship.application.internal.commandservices;

import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.application.internal.errors.SocialCompanionshipErrorMapper;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.storage.MediaStorageService;
import com.serenia.platform.socialcompanionship.domain.exceptions.SocialCompanionshipDomainException;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.PhotoMessage;
import com.serenia.platform.socialcompanionship.domain.model.commands.DiscardPhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.SelectPhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.SharePhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.ViewPhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.PhotoMessageId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.RelativeId;
import com.serenia.platform.socialcompanionship.domain.repositories.PhotoMessageRepository;
import com.serenia.platform.socialcompanionship.domain.services.PhotoMessageCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Application service that handles the upload, sharing, discarding and viewing of photo messages.
 */
@Service
@Slf4j
public class PhotoMessageCommandServiceImpl implements PhotoMessageCommandService {

    private static final String UNSUPPORTED_IMAGE = "photo.message.file.unsupported";
    private static final String FILE_TOO_LARGE = "validation.file.too.large";
    private static final String SENDER_NOT_LINKED = "photo.message.sender.not.linked";
    private static final String NOT_SENDER = "companion.message.not.sender";

    private final PhotoMessageRepository photoMessageRepository;
    private final MediaStorageService mediaStorageService;
    private final ExternalCareCircleService externalCareCircleService;
    private final DomainEventPublisher domainEventPublisher;
    private final DataSize maxFileSize;

    public PhotoMessageCommandServiceImpl(PhotoMessageRepository photoMessageRepository,
                                          MediaStorageService mediaStorageService,
                                          ExternalCareCircleService externalCareCircleService,
                                          DomainEventPublisher domainEventPublisher,
                                          @Value("${social-companionship.media.max-file-size}") DataSize maxFileSize) {
        this.photoMessageRepository = photoMessageRepository;
        this.mediaStorageService = mediaStorageService;
        this.externalCareCircleService = externalCareCircleService;
        this.domainEventPublisher = domainEventPublisher;
        this.maxFileSize = maxFileSize;
    }

    /** Checks that the relative is actively linked to the circle, stores the file and creates the draft. */
    @Override
    public Result<PhotoMessage, ApplicationError> handle(SelectPhotoMessageCommand command) {
        if (!command.photoFile().isSupportedImage())
            return Result.failure(ApplicationError.unsupportedMediaType(UNSUPPORTED_IMAGE));
        if (command.photoFile().size() > maxFileSize.toBytes())
            return Result.failure(ApplicationError.payloadTooLarge(FILE_TOO_LARGE));
        if (!isActiveRelativeOfCircle(command.senderId(), command.careCircleId()))
            return Result.failure(ApplicationError.forbidden(SENDER_NOT_LINKED));

        var mediaKey = mediaStorageService.store(command.photoFile());
        try {
            var message = PhotoMessage.select(new CareCircleId(command.careCircleId()),
                    new RelativeId(command.senderId()), mediaKey, Instant.now());
            return Result.success(saveAndPublish(message));
        } catch (RuntimeException e) {
            // The draft could not be saved: the stored file would be orphaned
            mediaStorageService.delete(mediaKey);
            throw e;
        }
    }

    /** Shares the draft with the older adult of the circle, as long as the sender is still linked. */
    @Override
    public Result<PhotoMessage, ApplicationError> handle(SharePhotoMessageCommand command) {
        var message = photoMessageRepository.findById(new PhotoMessageId(command.photoMessageId()));
        if (message.isEmpty()) return messageNotFound(command.photoMessageId());
        if (!message.get().isSentBy(command.senderId()))
            return Result.failure(ApplicationError.forbidden(NOT_SENDER));

        var careCircleId = message.get().getCareCircleId().value();
        var olderAdultId = externalCareCircleService.fetchOlderAdultId(careCircleId);
        if (olderAdultId.isEmpty() || !isActiveRelativeOfCircle(command.senderId(), careCircleId))
            return Result.failure(ApplicationError.forbidden(SENDER_NOT_LINKED));
        try {
            message.get().share(new OlderAdultId(olderAdultId.get()), Instant.now());
            return Result.success(saveAndPublish(message.get()));
        } catch (SocialCompanionshipDomainException e) {
            return Result.failure(SocialCompanionshipErrorMapper.toApplicationError(e));
        }
    }

    /** Deletes the draft and then its file, once the deletion of the record is confirmed. */
    @Override
    public Result<Void, ApplicationError> handle(DiscardPhotoMessageCommand command) {
        var message = photoMessageRepository.findById(new PhotoMessageId(command.photoMessageId()));
        if (message.isEmpty()) return messageNotFound(command.photoMessageId());
        if (!message.get().isSentBy(command.senderId()))
            return Result.failure(ApplicationError.forbidden(NOT_SENDER));
        try {
            message.get().discard(Instant.now());
        } catch (SocialCompanionshipDomainException e) {
            return Result.failure(SocialCompanionshipErrorMapper.toApplicationError(e));
        }
        photoMessageRepository.delete(message.get());
        publishEvents(message.get());
        try {
            mediaStorageService.delete(message.get().getMediaKey());
        } catch (RuntimeException e) {
            log.warn("Could not delete the file {} of discarded photo {}",
                    message.get().getMediaKey().value(), message.get().getId().value(), e);
        }
        return Result.success(null);
    }

    /** Records the view after checking that the requester is the older adult who received the photo. */
    @Override
    public Result<PhotoMessage, ApplicationError> handle(ViewPhotoMessageCommand command) {
        var message = photoMessageRepository.findById(new PhotoMessageId(command.photoMessageId()));
        if (message.isEmpty()) return messageNotFound(command.photoMessageId());
        try {
            message.get().view(new OlderAdultId(command.olderAdultId()), Instant.now());
            return Result.success(saveAndPublish(message.get()));
        } catch (SocialCompanionshipDomainException e) {
            return Result.failure(SocialCompanionshipErrorMapper.toApplicationError(e));
        }
    }

    private boolean isActiveRelativeOfCircle(UUID relativeId, UUID careCircleId) {
        return externalCareCircleService.fetchOlderAdultId(careCircleId)
                .map(olderAdultId -> externalCareCircleService.fetchActiveRelativeIds(olderAdultId).contains(relativeId))
                .orElse(false);
    }

    private PhotoMessage saveAndPublish(PhotoMessage message) {
        var saved = photoMessageRepository.save(message);
        publishEvents(message);
        return saved;
    }

    private void publishEvents(PhotoMessage message) {
        var events = List.copyOf(message.domainEvents());
        message.clearDomainEvents();
        domainEventPublisher.publish(events);
    }

    private static <T> Result<T, ApplicationError> messageNotFound(UUID photoMessageId) {
        return Result.failure(ApplicationError.notFound("photo_message", photoMessageId.toString()));
    }
}
