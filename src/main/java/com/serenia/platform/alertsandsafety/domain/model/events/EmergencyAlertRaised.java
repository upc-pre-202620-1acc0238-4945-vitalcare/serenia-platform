package com.serenia.platform.alertsandsafety.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the older adult pressed the help button.
 *
 * @param alertId the identifier of the alert
 * @param olderAdultId the older adult who asked for help
 * @param occurredAt the instant the button was pressed, in UTC
 */
public record EmergencyAlertRaised(UUID alertId, UUID olderAdultId, Instant occurredAt) {
}
