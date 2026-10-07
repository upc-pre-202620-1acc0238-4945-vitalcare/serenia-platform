package com.serenia.platform.iam.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when an account with the older adult role has been created.
 *
 * <p>Consumed by Care Circle, to create the care circle of the older adult, and by
 * Daily Check-in, to initialize its preferences.</p>
 *
 * @param userId     the identifier of the new account
 * @param occurredAt the instant the account was created, in UTC
 */
public record OlderAdultRegistered(UUID userId, Instant occurredAt) {
}
