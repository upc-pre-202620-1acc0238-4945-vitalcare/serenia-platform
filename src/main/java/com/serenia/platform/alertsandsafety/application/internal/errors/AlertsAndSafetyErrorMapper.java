package com.serenia.platform.alertsandsafety.application.internal.errors;

import com.serenia.platform.alertsandsafety.domain.exceptions.AlertsAndSafetyDomainException;
import com.serenia.platform.shared.application.result.ApplicationError;

/**
 * Translates the business-rule violations of the Alerts and Safety model into application errors.
 *
 * <p>Every violation means the alert does not admit the action in its current status, so all of
 * them are answered as a conflict.</p>
 */
public final class AlertsAndSafetyErrorMapper {

    private AlertsAndSafetyErrorMapper() {
    }

    public static ApplicationError toApplicationError(AlertsAndSafetyDomainException exception) {
        return ApplicationError.conflict("alert", exception.getMessage());
    }
}
