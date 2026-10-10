package com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects;

/**
 * Description of a small win; required and of at most 200 characters.
 *
 * @param value the trimmed text
 */
public record SmallWinDescription(String value) {
    public static final int MAX_LENGTH = 200;

    private static final String NOT_BLANK_MESSAGE_KEY = "small.win.description.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "small.win.description.too.long";

    public SmallWinDescription {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }
}
