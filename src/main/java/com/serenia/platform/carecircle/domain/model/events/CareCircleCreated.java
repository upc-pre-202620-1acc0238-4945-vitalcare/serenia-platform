package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the care circle of an older adult has been created.
 *
 * @param careCircleId the identifier of the new circle
 * @param olderAdultId the older adult who owns the circle
 * @param occurredAt   the instant the circle was created, in UTC
 */
public record CareCircleCreated(UUID careCircleId, UUID olderAdultId, Instant occurredAt) {
}
