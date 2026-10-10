package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the older adult recorded an audio, which stays as a draft.
 *
 * @param audioMessageId the identifier of the message
 * @param senderId the older adult who recorded it
 * @param occurredAt the instant of the recording, in UTC
 */
public record AudioMessageRecorded(UUID audioMessageId, UUID senderId, Instant occurredAt) {
}
