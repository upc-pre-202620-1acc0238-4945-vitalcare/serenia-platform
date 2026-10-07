package com.serenia.platform.dailycheckin.domain.services;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences;
import com.serenia.platform.dailycheckin.domain.model.commands.ActivateDailyPauseCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.DisableSimplifiedModeCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.EnableSimplifiedModeCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.InitializeCheckInPreferencesCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.ResumeDailyCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.ScheduleCheckInCommand;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

/**
 * Contract of the write operations on the check-in preferences.
 */
public interface CheckInPreferencesCommandService {

    /** Creates the preferences of an older adult, or returns the existing ones. */
    Result<CheckInPreferences, ApplicationError> handle(InitializeCheckInPreferencesCommand command);

    Result<CheckInPreferences, ApplicationError> handle(ScheduleCheckInCommand command);

    Result<CheckInPreferences, ApplicationError> handle(ActivateDailyPauseCommand command);

    Result<CheckInPreferences, ApplicationError> handle(ResumeDailyCheckInCommand command);

    Result<CheckInPreferences, ApplicationError> handle(EnableSimplifiedModeCommand command);

    Result<CheckInPreferences, ApplicationError> handle(DisableSimplifiedModeCommand command);
}
