package com.serenia.platform.shared.infrastructure.security;

import java.util.UUID;

/**
 * Principal stored in the security context for an authenticated request.
 *
 * <p>Built from the session and the token claims, so the full account is not loaded
 * on every request.</p>
 *
 * @param userId    the authenticated user
 * @param sessionId the session the request belongs to
 * @param role      the role of the user
 */
public record AuthenticatedUserPrincipal(UUID userId, UUID sessionId, String role) {
}
