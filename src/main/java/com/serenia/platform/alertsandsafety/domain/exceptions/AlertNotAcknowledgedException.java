package com.serenia.platform.alertsandsafety.domain.exceptions;

/**
 * Raised when resolving an alert that nobody acknowledged first.
 */
public class AlertNotAcknowledgedException extends AlertsAndSafetyDomainException {
    public AlertNotAcknowledgedException() {
        super("alert.not.acknowledged");
    }
}
