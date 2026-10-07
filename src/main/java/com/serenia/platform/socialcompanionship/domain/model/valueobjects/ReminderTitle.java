package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

/**
 * Title of a social reminder; required and of at most 120 characters.
 *
 * @param value the trimmed text
 */
public record ReminderTitle(String value) {
    public static final int MAX_LENGTH = 120;

    private static final String NOT_BLANK_MESSAGE_KEY = "social.reminder.title.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "social.reminder.title.too.long";

    public ReminderTitle {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }
}
