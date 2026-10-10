package com.serenia.platform.iam.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a user has explicitly closed one of their sessions.
 *
 * @param userId     the identifier of the session owner
 * @param sessionId  the identifier of the closed session
 * @param occurredAt the instant the session was closed, in UTC
 */
public record UserSessionClosed(UUID userId, UUID sessionId, Instant occurredAt) {
}
