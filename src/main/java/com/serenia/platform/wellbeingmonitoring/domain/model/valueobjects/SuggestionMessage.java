package com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects;

/**
 * Text of a wellbeing suggestion addressed to the relatives; required and of at most 300 characters.
 *
 * @param value the trimmed text
 */
public record SuggestionMessage(String value) {
    public static final int MAX_LENGTH = 300;

    private static final String NOT_BLANK_MESSAGE_KEY = "wellbeing.suggestion.message.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "wellbeing.suggestion.message.too.long";

    public SuggestionMessage {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }
}
