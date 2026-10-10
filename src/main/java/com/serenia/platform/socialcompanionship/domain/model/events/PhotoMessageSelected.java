package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a relative uploaded a photo, which stays as a draft.
 *
 * @param photoMessageId the identifier of the message
 * @param senderId the relative who uploaded it
 * @param occurredAt the instant of the upload, in UTC
 */
public record PhotoMessageSelected(UUID photoMessageId, UUID senderId, Instant occurredAt) {
}
