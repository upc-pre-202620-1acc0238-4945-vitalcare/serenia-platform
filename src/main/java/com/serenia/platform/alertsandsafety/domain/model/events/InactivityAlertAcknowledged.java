package com.serenia.platform.alertsandsafety.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a relative said they are attending the inactivity alert.
 *
 * @param alertId the identifier of the alert
 * @param relativeId the relative attending it
 * @param occurredAt the instant of the acknowledgement, in UTC
 */
public record InactivityAlertAcknowledged(UUID alertId, UUID relativeId, Instant occurredAt) {
}
