package com.serenia.platform.iam.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * BCrypt representation of a user's password.
 *
 * <p>The domain never receives raw passwords: callers must hash the raw input
 * through the hashing outbound service before building this value object.</p>
 *
 * @param value the BCrypt hash
 */
public record PasswordHash(String value) {
    private static final String NOT_BLANK_MESSAGE_KEY = "user.password.hash.blank";
    private static final String INVALID_MESSAGE_KEY = "user.password.hash.invalid";

    private static final Pattern BCRYPT_PATTERN =
            Pattern.compile("^\\$2[aby]?\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        if (!BCRYPT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
    }

    /** Hides the hash so it never ends up in logs. */
    @Override
    public String toString() {
        return "PasswordHash[****]";
    }
}
