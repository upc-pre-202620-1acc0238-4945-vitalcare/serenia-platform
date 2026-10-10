package com.serenia.platform.socialcompanionship.domain.services;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.PhotoMessage;
import com.serenia.platform.socialcompanionship.domain.model.commands.DiscardPhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.SelectPhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.SharePhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.ViewPhotoMessageCommand;

/**
 * Contract of the write operations on photo messages.
 */
public interface PhotoMessageCommandService {

    Result<PhotoMessage, ApplicationError> handle(SelectPhotoMessageCommand command);

    Result<PhotoMessage, ApplicationError> handle(SharePhotoMessageCommand command);

    Result<Void, ApplicationError> handle(DiscardPhotoMessageCommand command);

    Result<PhotoMessage, ApplicationError> handle(ViewPhotoMessageCommand command);
}
