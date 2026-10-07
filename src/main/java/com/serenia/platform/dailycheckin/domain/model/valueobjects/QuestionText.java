package com.serenia.platform.dailycheckin.domain.model.valueobjects;

/**
 * Text of a question presented to the older adult.
 *
 * @param value the trimmed text
 */
public record QuestionText(String value) {
    public static final int MAX_LENGTH = 200;

    private static final String NOT_BLANK_MESSAGE_KEY = "check.in.question.text.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "check.in.question.text.too.long";

    public QuestionText {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }
}
