package com.serenia.platform.alertsandsafety.domain.exceptions;

/**
 * Raised when an alert was already acknowledged; also thrown by the persistence adapter when two relatives acknowledge it at the same time.
 */
public class AlertAlreadyAcknowledgedException extends AlertsAndSafetyDomainException {
    public AlertAlreadyAcknowledgedException() {
        super("alert.already.acknowledged");
    }
}
