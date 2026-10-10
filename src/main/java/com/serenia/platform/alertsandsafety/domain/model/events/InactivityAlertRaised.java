package com.serenia.platform.alertsandsafety.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when an inactivity alert was raised.
 *
 * @param alertId the identifier of the alert
 * @param olderAdultId the older adult who did not answer
 * @param checkInId the missed check-in
 * @param occurredAt the instant the alert was raised, in UTC
 */
public record InactivityAlertRaised(UUID alertId, UUID olderAdultId, UUID checkInId, Instant occurredAt) {
}
