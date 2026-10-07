package com.serenia.platform.iam.domain.model.commands;

import java.util.UUID;

/**
 * Intention of revoking the active sessions of a user other than the one
 * that performed the password change.
 *
 * @param userId             the sessions owner
 * @param preservedSessionId the session that must remain active
 */
public record RevokeSessionsCommand(UUID userId, UUID preservedSessionId) {
}
