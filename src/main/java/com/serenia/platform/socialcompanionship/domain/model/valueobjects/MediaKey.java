package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

/**
 * Key of a file in the media storage; it is not a public URL.
 *
 * @param value the trimmed text
 */
public record MediaKey(String value) {
    public static final int MAX_LENGTH = 500;

    private static final String NOT_BLANK_MESSAGE_KEY = "media.key.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "media.key.too.long";

    public MediaKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }
}
