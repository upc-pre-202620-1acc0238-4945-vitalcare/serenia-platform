package com.serenia.platform.socialcompanionship.application.internal.commandservices;

import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.application.internal.errors.SocialCompanionshipErrorMapper;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.storage.MediaStorageService;
import com.serenia.platform.socialcompanionship.domain.exceptions.SocialCompanionshipDomainException;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.AudioMessage;
import com.serenia.platform.socialcompanionship.domain.model.commands.DiscardAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.PlayAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.RecordAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.ShareAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.AudioDuration;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.AudioMessageId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.RelativeId;
import com.serenia.platform.socialcompanionship.domain.repositories.AudioMessageRepository;
import com.serenia.platform.socialcompanionship.domain.services.AudioMessageCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Application service that handles the recording, sharing, discarding and playing of audio messages.
 */
@Service
@Slf4j
public class AudioMessageCommandServiceImpl implements AudioMessageCommandService {

    private static final String UNSUPPORTED_AUDIO = "audio.message.file.unsupported";
    private static final String FILE_TOO_LARGE = "validation.file.too.large";
    private static final String SENDER_WITHOUT_CARE_CIRCLE = "audio.message.sender.without.care.circle";
    private static final String NOT_SENDER = "companion.message.not.sender";

    private final AudioMessageRepository audioMessageRepository;
    private final MediaStorageService mediaStorageService;
    private final ExternalCareCircleService externalCareCircleService;
    private final DomainEventPublisher domainEventPublisher;
    private final DataSize maxFileSize;

    public AudioMessageCommandServiceImpl(AudioMessageRepository audioMessageRepository,
                                          MediaStorageService mediaStorageService,
                                          ExternalCareCircleService externalCareCircleService,
                                          DomainEventPublisher domainEventPublisher,
                                          @Value("${social-companionship.media.max-file-size}") DataSize maxFileSize) {
        this.audioMessageRepository = audioMessageRepository;
        this.mediaStorageService = mediaStorageService;
        this.externalCareCircleService = externalCareCircleService;
        this.domainEventPublisher = domainEventPublisher;
        this.maxFileSize = maxFileSize;
    }

    /** Resolves the older adult's circle, stores the file and creates the draft. */
    @Override
    public Result<AudioMessage, ApplicationError> handle(RecordAudioMessageCommand command) {
        if (!command.audioFile().isSupportedAudio())
            return Result.failure(ApplicationError.unsupportedMediaType(UNSUPPORTED_AUDIO));
        if (command.audioFile().size() > maxFileSize.toBytes())
            return Result.failure(ApplicationError.payloadTooLarge(FILE_TOO_LARGE));
        var duration = new AudioDuration(command.durationSeconds());

        var careCircleId = externalCareCircleService.fetchCareCircleId(command.senderId());
        if (careCircleId.isEmpty())
            return Result.failure(ApplicationError.forbidden(SENDER_WITHOUT_CARE_CIRCLE));

        var mediaKey = mediaStorageService.store(command.audioFile());
        try {
            var message = AudioMessage.record(new CareCircleId(careCircleId.get()),
                    new OlderAdultId(command.senderId()), mediaKey, duration, Instant.now());
            return Result.success(saveAndPublish(message));
        } catch (RuntimeException e) {
            // The draft could not be saved: the stored file would be orphaned
            mediaStorageService.delete(mediaKey);
            throw e;
        }
    }

    /** Shares the draft with the relatives actively linked to the older adult at this moment. */
    @Override
    public Result<AudioMessage, ApplicationError> handle(ShareAudioMessageCommand command) {
        return changeOwnMessage(command.audioMessageId(), command.senderId(), message -> {
            var recipients = externalCareCircleService.fetchActiveRelativeIds(command.senderId()).stream()
                    .map(RelativeId::new)
                    .toList();
            message.share(recipients, Instant.now());
        });
    }

    /** Deletes the draft and then its file, once the deletion of the record is confirmed. */
    @Override
    public Result<Void, ApplicationError> handle(DiscardAudioMessageCommand command) {
        var message = audioMessageRepository.findById(new AudioMessageId(command.audioMessageId()));
        if (message.isEmpty()) return messageNotFound(command.audioMessageId());
        if (!message.get().isSentBy(command.senderId()))
            return Result.failure(ApplicationError.forbidden(NOT_SENDER));
        try {
            message.get().discard(Instant.now());
        } catch (SocialCompanionshipDomainException e) {
            return Result.failure(SocialCompanionshipErrorMapper.toApplicationError(e));
        }
        audioMessageRepository.delete(message.get());
        publishEvents(message.get());
        deleteFileQuietly(message.get());
        return Result.success(null);
    }

    /** Records the play after checking that the relative is a recipient of the audio. */
    @Override
    public Result<AudioMessage, ApplicationError> handle(PlayAudioMessageCommand command) {
        var message = audioMessageRepository.findById(new AudioMessageId(command.audioMessageId()));
        if (message.isEmpty()) return messageNotFound(command.audioMessageId());
        try {
            message.get().play(new RelativeId(command.relativeId()), Instant.now());
            return Result.success(saveAndPublish(message.get()));
        } catch (SocialCompanionshipDomainException e) {
            return Result.failure(SocialCompanionshipErrorMapper.toApplicationError(e));
        }
    }

    private Result<AudioMessage, ApplicationError> changeOwnMessage(UUID audioMessageId, UUID senderId,
                                                                    Consumer<AudioMessage> change) {
        var message = audioMessageRepository.findById(new AudioMessageId(audioMessageId));
        if (message.isEmpty()) return messageNotFound(audioMessageId);
        if (!message.get().isSentBy(senderId))
            return Result.failure(ApplicationError.forbidden(NOT_SENDER));
        try {
            change.accept(message.get());
            return Result.success(saveAndPublish(message.get()));
        } catch (SocialCompanionshipDomainException e) {
            return Result.failure(SocialCompanionshipErrorMapper.toApplicationError(e));
        }
    }

    private void deleteFileQuietly(AudioMessage message) {
        try {
            mediaStorageService.delete(message.getMediaKey());
        } catch (RuntimeException e) {
            log.warn("Could not delete the file {} of discarded audio {}", message.getMediaKey().value(), message.getId().value(), e);
        }
    }

    private AudioMessage saveAndPublish(AudioMessage message) {
        var saved = audioMessageRepository.save(message);
        publishEvents(message);
        return saved;
    }

    private void publishEvents(AudioMessage message) {
        var events = List.copyOf(message.domainEvents());
        message.clearDomainEvents();
        domainEventPublisher.publish(events);
    }

    private static <T> Result<T, ApplicationError> messageNotFound(UUID audioMessageId) {
        return Result.failure(ApplicationError.notFound("audio_message", audioMessageId.toString()));
    }
}
