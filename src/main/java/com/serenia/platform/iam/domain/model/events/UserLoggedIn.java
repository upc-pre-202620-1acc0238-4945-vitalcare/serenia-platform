package com.serenia.platform.iam.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a user has authenticated and opened a session.
 *
 * @param userId     the identifier of the authenticated user
 * @param sessionId  the identifier of the opened session
 * @param occurredAt the instant the session was opened, in UTC
 */
public record UserLoggedIn(UUID userId, UUID sessionId, Instant occurredAt) {
}
