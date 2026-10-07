package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the older adult discarded an audio before sharing it.
 *
 * @param audioMessageId the identifier of the message
 * @param senderId the older adult who discarded it
 * @param occurredAt the instant of the discard, in UTC
 */
public record AudioMessageDiscarded(UUID audioMessageId, UUID senderId, Instant occurredAt) {
}
