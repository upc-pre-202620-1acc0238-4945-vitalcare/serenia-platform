package com.serenia.platform.alertsandsafety.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the emergency could not be notified because the older adult has no linked relatives.
 *
 * @param alertId the identifier of the alert
 * @param olderAdultId the older adult who asked for help
 * @param occurredAt the instant of the failed dispatch, in UTC
 */
public record EmergencyAlertDispatchFailed(UUID alertId, UUID olderAdultId, Instant occurredAt) {
}
