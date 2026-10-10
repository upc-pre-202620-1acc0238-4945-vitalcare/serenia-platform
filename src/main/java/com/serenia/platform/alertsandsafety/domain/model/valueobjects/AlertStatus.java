package com.serenia.platform.alertsandsafety.domain.model.valueobjects;

/**
 * Status of an alert. An emergency goes through RAISED, DISPATCHED or DISPATCH_FAILED, ACKNOWLEDGED and RESOLVED; an inactivity alert, through RAISED, ACKNOWLEDGED and RESOLVED.
 */
public enum AlertStatus {
    RAISED,
    DISPATCHED,
    DISPATCH_FAILED,
    ACKNOWLEDGED,
    RESOLVED
}
