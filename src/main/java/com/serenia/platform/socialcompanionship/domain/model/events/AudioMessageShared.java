package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain event raised when the audio was shared with the linked relatives.
 *
 * @param audioMessageId the identifier of the message
 * @param careCircleId the circle it was shared in
 * @param recipientIds the relatives who received it
 * @param occurredAt the instant it was shared, in UTC
 */
public record AudioMessageShared(UUID audioMessageId, UUID careCircleId, List<UUID> recipientIds, Instant occurredAt) {
}
