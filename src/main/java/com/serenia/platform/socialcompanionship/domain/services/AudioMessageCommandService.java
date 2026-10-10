package com.serenia.platform.socialcompanionship.domain.services;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.AudioMessage;
import com.serenia.platform.socialcompanionship.domain.model.commands.DiscardAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.PlayAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.RecordAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.ShareAudioMessageCommand;

/**
 * Contract of the write operations on audio messages.
 */
public interface AudioMessageCommandService {

    Result<AudioMessage, ApplicationError> handle(RecordAudioMessageCommand command);

    Result<AudioMessage, ApplicationError> handle(ShareAudioMessageCommand command);

    Result<Void, ApplicationError> handle(DiscardAudioMessageCommand command);

    Result<AudioMessage, ApplicationError> handle(PlayAudioMessageCommand command);
}
