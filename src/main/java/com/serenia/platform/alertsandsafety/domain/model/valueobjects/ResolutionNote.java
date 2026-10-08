package com.serenia.platform.alertsandsafety.domain.model.valueobjects;

/**
 * Note describing how an alert was resolved.
 *
 * @param value the trimmed note, of at most 300 characters
 */
public record ResolutionNote(String value) {
    public static final int MAX_LENGTH = 300;

    private static final String NOT_BLANK_MESSAGE_KEY = "alert.resolution.note.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "alert.resolution.note.too.long";

    public ResolutionNote {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }

    /**
     * Builds a note from an optional raw text.
     *
     * @return the note, or {@code null} when no text was provided
     */
    public static ResolutionNote fromNullable(String value) {
        return value == null || value.isBlank() ? null : new ResolutionNote(value);
    }
}
