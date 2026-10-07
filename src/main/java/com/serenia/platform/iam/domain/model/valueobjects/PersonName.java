package com.serenia.platform.iam.domain.model.valueobjects;

/**
 * Full name of a user.
 *
 * @param value the trimmed full name
 */
public record PersonName(String value) {
    public static final int MAX_LENGTH = 120;

    private static final String NOT_BLANK_MESSAGE_KEY = "user.full.name.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "user.full.name.too.long";

    public PersonName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }
}
