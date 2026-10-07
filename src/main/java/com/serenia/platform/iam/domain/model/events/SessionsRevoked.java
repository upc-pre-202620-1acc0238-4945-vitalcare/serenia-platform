package com.serenia.platform.iam.domain.model.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain event raised when the other active sessions of a user have been revoked
 * after a password change.
 *
 * @param userId            the identifier of the sessions owner
 * @param revokedSessionIds the identifiers of the revoked sessions
 * @param occurredAt        the instant of the revocation, in UTC
 */
public record SessionsRevoked(UUID userId, List<UUID> revokedSessionIds, Instant occurredAt) {
    public SessionsRevoked {
        revokedSessionIds = List.copyOf(revokedSessionIds);
    }
}
