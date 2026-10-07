package com.serenia.platform.socialcompanionship.domain.services;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.PhotoMessage;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetPhotoMessageMediaQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSharedPhotoMessagesByCareCircleIdQuery;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaFile;

import java.util.List;

/**
 * Contract of the read operations on photo messages.
 */
public interface PhotoMessageQueryService {

    Result<List<PhotoMessage>, ApplicationError> handle(GetSharedPhotoMessagesByCareCircleIdQuery query);

    Result<MediaFile, ApplicationError> handle(GetPhotoMessageMediaQuery query);
}
