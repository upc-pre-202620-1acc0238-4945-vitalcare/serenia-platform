package com.serenia.platform.wellbeingmonitoring.application.internal.errors;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.wellbeingmonitoring.domain.exceptions.SmallWinAlreadyRecordedException;
import com.serenia.platform.wellbeingmonitoring.domain.exceptions.WellbeingMonitoringDomainException;
import com.serenia.platform.wellbeingmonitoring.domain.exceptions.WellbeingPatternNotFoundException;

/**
 * Translates the business-rule violations of the Wellbeing Monitoring model into application errors.
 */
public final class WellbeingMonitoringErrorMapper {

    private WellbeingMonitoringErrorMapper() {
    }

    public static ApplicationError toApplicationError(WellbeingMonitoringDomainException exception) {
        var messageKey = exception.getMessage();
        return switch (exception) {
            case WellbeingPatternNotFoundException _ -> ApplicationError.notFound("wellbeing_pattern", messageKey);
            case SmallWinAlreadyRecordedException _ -> ApplicationError.conflict("small_win", messageKey);
            default -> ApplicationError.conflict("wellbeing_suggestion", messageKey);
        };
    }
}
