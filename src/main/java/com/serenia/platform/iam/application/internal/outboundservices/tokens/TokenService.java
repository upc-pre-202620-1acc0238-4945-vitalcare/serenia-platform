package com.serenia.platform.iam.application.internal.outboundservices.tokens;

import java.time.Instant;
import java.util.UUID;

/**
 * Outbound port for the issuance, validation and hashing of session tokens.
 *
 * <p>Decouples the application layer from the token library so the infrastructure
 * implementation can be swapped without touching the domain.</p>
 */
public interface TokenService {

    /**
     * Issues a signed token for a session.
     *
     * @param userId    the owner of the session
     * @param sessionId the session the token belongs to
     * @param role      the role of the user
     * @param expiresAt the instant the token stops being valid
     * @return the signed token
     */
    String generateToken(UUID userId, UUID sessionId, String role, Instant expiresAt);

    /**
     * Validates the token signature, expiration and structure.
     *
     * @param token the signed token
     * @return {@code true} when the token is valid
     */
    boolean validateToken(String token);

    /**
     * Extracts the session identifier carried by a valid token.
     *
     * @param token the signed token
     * @return the session identifier
     */
    UUID extractSessionId(String token);

    /**
     * Computes the hash stored for a token.
     *
     * @param token the signed token
     * @return the hexadecimal hash of the token
     */
    String hash(String token);
}
