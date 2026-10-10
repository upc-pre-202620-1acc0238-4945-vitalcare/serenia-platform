package com.serenia.platform.alertsandsafety.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the emergency became available to the relatives and was confirmed to the older adult.
 *
 * @param alertId the identifier of the alert
 * @param olderAdultId the older adult who asked for help
 * @param occurredAt the instant of the confirmation, in UTC
 */
public record EmergencyAlertDeliveryConfirmed(UUID alertId, UUID olderAdultId, Instant occurredAt) {
}
