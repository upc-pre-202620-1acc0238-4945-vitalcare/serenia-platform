package com.serenia.platform.socialcompanionship.domain.services;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.AudioMessage;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetAudioMessageMediaQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSharedAudioMessagesByCareCircleIdQuery;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;

import java.util.List;

/**
 * Contract of the read operations on audio messages.
 */
public interface AudioMessageQueryService {

    Result<List<AudioMessage>, ApplicationError> handle(GetSharedAudioMessagesByCareCircleIdQuery query);

    Result<MediaFile, ApplicationError> handle(GetAudioMessageMediaQuery query);
}
