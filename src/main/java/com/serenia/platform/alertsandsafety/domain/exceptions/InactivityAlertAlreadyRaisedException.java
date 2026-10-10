package com.serenia.platform.alertsandsafety.domain.exceptions;

/**
 * Raised when the missed check-in already produced an alert; thrown by the persistence adapter on the unique index of the check-in.
 */
public class InactivityAlertAlreadyRaisedException extends AlertsAndSafetyDomainException {
    public InactivityAlertAlreadyRaisedException() {
        super("alert.inactivity.already.raised");
    }
}
