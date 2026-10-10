package com.serenia.platform.alertsandsafety.domain.exceptions;

/**
 * Raised when the status of an alert does not allow the requested action.
 */
public class InvalidAlertStateException extends AlertsAndSafetyDomainException {
    public InvalidAlertStateException() {
        super("alert.invalid.state");
    }
}
