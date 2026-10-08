package com.serenia.platform.alertsandsafety.domain.model.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain event raised when the emergency was notified to every linked relative.
 *
 * @param alertId the identifier of the alert
 * @param olderAdultId the older adult who asked for help
 * @param relativeIds the notified relatives
 * @param occurredAt the instant of the dispatch, in UTC
 */
public record EmergencyAlertDispatched(UUID alertId, UUID olderAdultId, List<UUID> relativeIds, Instant occurredAt) {
}
