package com.serenia.platform.iam.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a user's personal data has been updated.
 *
 * @param userId     the identifier of the user
 * @param occurredAt the instant of the update, in UTC
 */
public record ProfileDataUpdated(UUID userId, Instant occurredAt) {
}
