package com.serenia.platform.socialcompanionship.application.internal.queryservices;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.storage.MediaStorageService;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.PhotoMessage;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetPhotoMessageMediaQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSharedPhotoMessagesByCareCircleIdQuery;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.PhotoMessageId;
import com.serenia.platform.socialcompanionship.domain.repositories.PhotoMessageRepository;
import com.serenia.platform.socialcompanionship.domain.services.PhotoMessageQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service that resolves the shared photos of a circle, after checking access, and
 * hands the file of a photo only to its sender or its recipient.
 */
@Service
public class PhotoMessageQueryServiceImpl implements PhotoMessageQueryService {

    private static final String NO_ACCESS = "companion.care.circle.access.denied";
    private static final String NOT_SENDER_NOR_RECIPIENT = "companion.message.not.sender.nor.recipient";
    private static final String MEDIA_NOT_FOUND = "companion.message.media.not.found";

    private final PhotoMessageRepository photoMessageRepository;
    private final MediaStorageService mediaStorageService;
    private final ExternalCareCircleService externalCareCircleService;

    public PhotoMessageQueryServiceImpl(PhotoMessageRepository photoMessageRepository,
                                        MediaStorageService mediaStorageService,
                                        ExternalCareCircleService externalCareCircleService) {
        this.photoMessageRepository = photoMessageRepository;
        this.mediaStorageService = mediaStorageService;
        this.externalCareCircleService = externalCareCircleService;
    }

    /**
     * Returns the shared photos the requester can see: the whole gallery for the older adult who
     * received them and, for a relative, those they sent.
     */
    @Override
    public Result<List<PhotoMessage>, ApplicationError> handle(GetSharedPhotoMessagesByCareCircleIdQuery query) {
        if (!externalCareCircleService.hasAccessToCareCircle(query.requesterId(), query.careCircleId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));
        return Result.success(photoMessageRepository.findAllSharedByCareCircleId(new CareCircleId(query.careCircleId()))
                .stream()
                .filter(message -> message.isSentBy(query.requesterId()) || message.isRecipient(query.requesterId()))
                .toList());
    }

    @Override
    public Result<MediaFile, ApplicationError> handle(GetPhotoMessageMediaQuery query) {
        var message = photoMessageRepository.findById(new PhotoMessageId(query.photoMessageId()));
        if (message.isEmpty())
            return Result.failure(ApplicationError.notFound("photo_message", query.photoMessageId().toString()));
        if (!message.get().isSentBy(query.requesterId()) && !message.get().isRecipient(query.requesterId()))
            return Result.failure(ApplicationError.forbidden(NOT_SENDER_NOR_RECIPIENT));
        return mediaStorageService.load(message.get().getMediaKey())
                .<Result<MediaFile, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("photo_media", MEDIA_NOT_FOUND)));
    }
}
