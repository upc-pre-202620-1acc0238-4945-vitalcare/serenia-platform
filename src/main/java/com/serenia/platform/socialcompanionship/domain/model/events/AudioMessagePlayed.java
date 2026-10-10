package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a relative played the audio for the first time.
 *
 * @param audioMessageId the identifier of the message
 * @param relativeId the relative who played it
 * @param occurredAt the instant of the first play, in UTC
 */
public record AudioMessagePlayed(UUID audioMessageId, UUID relativeId, Instant occurredAt) {
}
