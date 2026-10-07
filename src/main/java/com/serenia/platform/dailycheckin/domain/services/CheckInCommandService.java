package com.serenia.platform.dailycheckin.domain.services;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.domain.model.commands.AnswerCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.ExpireCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.OpenCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.PromptCheckInCommand;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

/**
 * Contract of the write operations on daily check-ins.
 */
public interface CheckInCommandService {

    /** Opens the check-in of the current day, or returns the existing one. */
    Result<CheckIn, ApplicationError> handle(OpenCheckInCommand command);

    Result<Void, ApplicationError> handle(PromptCheckInCommand command);

    Result<CheckIn, ApplicationError> handle(AnswerCheckInCommand command);

    Result<Void, ApplicationError> handle(ExpireCheckInCommand command);
}
