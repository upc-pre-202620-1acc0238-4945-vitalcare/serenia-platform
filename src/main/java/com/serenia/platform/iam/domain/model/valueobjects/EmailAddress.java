package com.serenia.platform.iam.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Email address used by a user to sign in.
 *
 * <p>The value is trimmed and normalized to lower case so that uniqueness
 * does not depend on letter casing.</p>
 *
 * @param value the normalized email address
 */
public record EmailAddress(String value) {
    public static final int MAX_LENGTH = 160;

    private static final String NOT_BLANK_MESSAGE_KEY = "user.email.blank";
    private static final String INVALID_MESSAGE_KEY = "user.email.invalid";
    private static final String TOO_LONG_MESSAGE_KEY = "user.email.too.long";

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-z0-9+_.-]+@[a-z0-9.-]+\\.[a-z]{2,}$");

    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
    }
}
