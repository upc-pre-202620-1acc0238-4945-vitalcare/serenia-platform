package com.serenia.platform.alertsandsafety.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a relative marked the inactivity alert as resolved.
 *
 * @param alertId the identifier of the alert
 * @param relativeId the relative who resolved it
 * @param occurredAt the instant of the resolution, in UTC
 */
public record InactivityAlertResolved(UUID alertId, UUID relativeId, Instant occurredAt) {
}
