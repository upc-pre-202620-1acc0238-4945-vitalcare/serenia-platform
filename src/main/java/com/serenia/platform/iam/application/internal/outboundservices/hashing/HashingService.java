package com.serenia.platform.iam.application.internal.outboundservices.hashing;

/**
 * Outbound port for password hashing and verification.
 */
public interface HashingService {

    /**
     * Hashes a raw password.
     *
     * @param rawPassword the password to hash
     * @return the hashed password
     */
    String encode(CharSequence rawPassword);

    /**
     * Verifies a raw password against a stored hash.
     *
     * @param rawPassword  the raw password
     * @param passwordHash the stored hash
     * @return {@code true} if the raw password matches the hash
     */
    boolean matches(CharSequence rawPassword, String passwordHash);
}
