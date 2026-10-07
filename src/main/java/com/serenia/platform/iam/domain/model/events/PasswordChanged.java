package com.serenia.platform.iam.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised after a user's password has been changed.
 *
 * <p>The new password is deliberately not part of the payload. The preserved session
 * is the one from which the change was made; every other active session is revoked.</p>
 *
 * @param userId             the identifier of the user
 * @param preservedSessionId the session from which the password was changed
 * @param occurredAt         the instant of the change, in UTC
 */
public record PasswordChanged(UUID userId, UUID preservedSessionId, Instant occurredAt) {
}
