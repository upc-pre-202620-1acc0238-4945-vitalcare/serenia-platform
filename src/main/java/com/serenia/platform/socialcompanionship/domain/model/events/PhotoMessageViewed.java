package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the older adult saw the photo for the first time.
 *
 * @param photoMessageId the identifier of the message
 * @param olderAdultId the older adult who saw it
 * @param occurredAt the instant of the first view, in UTC
 */
public record PhotoMessageViewed(UUID photoMessageId, UUID olderAdultId, Instant occurredAt) {
}
