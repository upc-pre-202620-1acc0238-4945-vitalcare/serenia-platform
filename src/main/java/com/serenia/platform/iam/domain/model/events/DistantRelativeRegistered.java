package com.serenia.platform.iam.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when an account with the distant relative role has been created.
 *
 * @param userId     the identifier of the new account
 * @param occurredAt the instant the account was created, in UTC
 */
public record DistantRelativeRegistered(UUID userId, Instant occurredAt) {
}
