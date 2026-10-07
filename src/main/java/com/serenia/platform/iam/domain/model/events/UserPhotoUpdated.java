package com.serenia.platform.iam.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a user's profile photo has been replaced.
 *
 * @param userId     the identifier of the user
 * @param occurredAt the instant of the update, in UTC
 */
public record UserPhotoUpdated(UUID userId, Instant occurredAt) {
}
