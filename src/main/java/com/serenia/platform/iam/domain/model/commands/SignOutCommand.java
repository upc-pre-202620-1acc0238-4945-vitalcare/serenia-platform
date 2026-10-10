package com.serenia.platform.iam.domain.model.commands;

import java.util.UUID;

/**
 * Intention of closing an active session of the requesting user.
 *
 * @param sessionId the session to close
 * @param userId    the authenticated user requesting the sign-out
 */
public record SignOutCommand(UUID sessionId, UUID userId) {
}
