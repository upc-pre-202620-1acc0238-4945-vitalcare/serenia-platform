package com.serenia.platform.iam.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Language and region code of the user interface (for example, {@code es-PE}).
 *
 * @param value the locale code
 */
public record LocaleCode(String value) {
    public static final int MAX_LENGTH = 10;

    private static final String NOT_BLANK_MESSAGE_KEY = "user.locale.blank";
    private static final String INVALID_MESSAGE_KEY = "user.locale.invalid";

    private static final Pattern LOCALE_PATTERN = Pattern.compile("^[a-zA-Z]{2,3}([-_][a-zA-Z0-9]{2,4})?$");

    public LocaleCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH || !LOCALE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
    }
}
