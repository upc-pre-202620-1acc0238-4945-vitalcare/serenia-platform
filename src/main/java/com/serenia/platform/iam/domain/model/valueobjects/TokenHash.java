package com.serenia.platform.iam.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * SHA-256 hash of a session token, encoded as 64 hexadecimal characters.
 *
 * <p>Only the hash is stored, so a leaked sessions table cannot be used
 * to impersonate users.</p>
 *
 * @param value the lower-case hexadecimal hash
 */
public record TokenHash(String value) {
    private static final String INVALID_MESSAGE_KEY = "session.token.hash.invalid";

    private static final Pattern SHA_256_HEX_PATTERN = Pattern.compile("^[0-9a-f]{64}$");

    public TokenHash {
        if (value == null) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
        value = value.toLowerCase(Locale.ROOT);
        if (!SHA_256_HEX_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
    }
}
