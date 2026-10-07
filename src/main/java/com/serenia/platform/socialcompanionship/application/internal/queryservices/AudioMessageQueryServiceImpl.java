package com.serenia.platform.socialcompanionship.application.internal.queryservices;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.storage.MediaStorageService;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.AudioMessage;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetAudioMessageMediaQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSharedAudioMessagesByCareCircleIdQuery;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.AudioMessageId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;
import com.serenia.platform.socialcompanionship.domain.repositories.AudioMessageRepository;
import com.serenia.platform.socialcompanionship.domain.services.AudioMessageQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service that resolves the shared audios of a circle, after checking access, and
 * hands the file of an audio only to its sender or its recipients.
 */
@Service
public class AudioMessageQueryServiceImpl implements AudioMessageQueryService {

    private static final String NO_ACCESS = "companion.care.circle.access.denied";
    private static final String NOT_SENDER_NOR_RECIPIENT = "companion.message.not.sender.nor.recipient";
    private static final String MEDIA_NOT_FOUND = "companion.message.media.not.found";

    private final AudioMessageRepository audioMessageRepository;
    private final MediaStorageService mediaStorageService;
    private final ExternalCareCircleService externalCareCircleService;

    public AudioMessageQueryServiceImpl(AudioMessageRepository audioMessageRepository,
                                        MediaStorageService mediaStorageService,
                                        ExternalCareCircleService externalCareCircleService) {
        this.audioMessageRepository = audioMessageRepository;
        this.mediaStorageService = mediaStorageService;
        this.externalCareCircleService = externalCareCircleService;
    }

    /**
     * Returns the shared audios the requester can play: every audio for the older adult who
     * recorded them and, for a relative, those they received.
     */
    @Override
    public Result<List<AudioMessage>, ApplicationError> handle(GetSharedAudioMessagesByCareCircleIdQuery query) {
        if (!externalCareCircleService.hasAccessToCareCircle(query.requesterId(), query.careCircleId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));
        return Result.success(audioMessageRepository.findAllSharedByCareCircleId(new CareCircleId(query.careCircleId()))
                .stream()
                .filter(message -> message.isSentBy(query.requesterId()) || message.isRecipient(query.requesterId()))
                .toList());
    }

    @Override
    public Result<MediaFile, ApplicationError> handle(GetAudioMessageMediaQuery query) {
        var message = audioMessageRepository.findById(new AudioMessageId(query.audioMessageId()));
        if (message.isEmpty())
            return Result.failure(ApplicationError.notFound("audio_message", query.audioMessageId().toString()));
        if (!message.get().isSentBy(query.requesterId()) && !message.get().isRecipient(query.requesterId()))
            return Result.failure(ApplicationError.forbidden(NOT_SENDER_NOR_RECIPIENT));
        return mediaStorageService.load(message.get().getMediaKey())
                .<Result<MediaFile, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("audio_media", MEDIA_NOT_FOUND)));
    }
}
