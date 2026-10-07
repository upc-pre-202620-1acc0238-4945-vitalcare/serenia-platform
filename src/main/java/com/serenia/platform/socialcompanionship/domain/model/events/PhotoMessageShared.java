package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the photo was shared with the older adult.
 *
 * @param photoMessageId the identifier of the message
 * @param careCircleId the circle it was shared in
 * @param olderAdultId the older adult who received it
 * @param occurredAt the instant it was shared, in UTC
 */
public record PhotoMessageShared(UUID photoMessageId, UUID careCircleId, UUID olderAdultId, Instant occurredAt) {
}
