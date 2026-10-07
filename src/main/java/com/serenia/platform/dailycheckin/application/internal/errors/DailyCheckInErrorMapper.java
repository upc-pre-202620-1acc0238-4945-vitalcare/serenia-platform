package com.serenia.platform.dailycheckin.application.internal.errors;

import com.serenia.platform.dailycheckin.domain.exceptions.CheckInDeadlineNotReachedException;
import com.serenia.platform.dailycheckin.domain.exceptions.DailyCheckInDomainException;
import com.serenia.platform.dailycheckin.domain.exceptions.DailyCheckInNotPausedException;
import com.serenia.platform.dailycheckin.domain.exceptions.DailyPauseAlreadyActiveException;
import com.serenia.platform.dailycheckin.domain.exceptions.InvalidDeadlineExtensionException;
import com.serenia.platform.shared.application.result.ApplicationError;

/**
 * Translates the business-rule violations of the Daily Check-in model into application errors,
 * so every command service maps them to the same HTTP status.
 */
public final class DailyCheckInErrorMapper {

    private DailyCheckInErrorMapper() {
    }

    public static ApplicationError toApplicationError(DailyCheckInDomainException exception) {
        var messageKey = exception.getMessage();
        return switch (exception) {
            case DailyPauseAlreadyActiveException _, DailyCheckInNotPausedException _ ->
                    ApplicationError.conflict("daily_pause", messageKey);
            case InvalidDeadlineExtensionException _, CheckInDeadlineNotReachedException _ ->
                    ApplicationError.businessRuleViolation("check_in", messageKey);
            default -> ApplicationError.conflict("check_in", messageKey);
        };
    }
}
