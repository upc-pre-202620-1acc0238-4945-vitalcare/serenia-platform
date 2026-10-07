package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the relative discarded a photo before sharing it.
 *
 * @param photoMessageId the identifier of the message
 * @param senderId the relative who discarded it
 * @param occurredAt the instant of the discard, in UTC
 */
public record PhotoMessageDiscarded(UUID photoMessageId, UUID senderId, Instant occurredAt) {
}
