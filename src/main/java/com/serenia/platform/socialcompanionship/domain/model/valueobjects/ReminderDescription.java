package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

/**
 * Description of a social reminder, of at most 300 characters.
 *
 * @param value the trimmed text
 */
public record ReminderDescription(String value) {
    public static final int MAX_LENGTH = 300;

    private static final String NOT_BLANK_MESSAGE_KEY = "social.reminder.description.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "social.reminder.description.too.long";

    public ReminderDescription {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }

    /**
     * Builds the value from an optional raw text.
     *
     * @return the value, or {@code null} when no text was provided
     */
    public static ReminderDescription fromNullable(String value) {
        return value == null || value.isBlank() ? null : new ReminderDescription(value);
    }
}
