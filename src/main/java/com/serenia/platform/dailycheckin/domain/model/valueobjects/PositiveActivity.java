package com.serenia.platform.dailycheckin.domain.model.valueobjects;

/**
 * Positive activity reported by the older adult together with the answer.
 *
 * @param value the trimmed activity description
 */
public record PositiveActivity(String value) {
    public static final int MAX_LENGTH = 200;

    private static final String NOT_BLANK_MESSAGE_KEY = "check.in.positive.activity.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "check.in.positive.activity.too.long";

    public PositiveActivity {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }

    /**
     * Builds a positive activity from an optional raw value.
     *
     * @param value raw activity, possibly {@code null} or blank
     * @return the activity, or {@code null} when no value was provided
     */
    public static PositiveActivity fromNullable(String value) {
        return value == null || value.isBlank() ? null : new PositiveActivity(value);
    }
}
