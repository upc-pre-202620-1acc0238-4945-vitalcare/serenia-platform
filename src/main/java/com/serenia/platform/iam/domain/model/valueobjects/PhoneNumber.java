package com.serenia.platform.iam.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Contact phone number in E.164 format (for example, {@code +51987654321}).
 *
 * @param value the phone number
 */
public record PhoneNumber(String value) {
    public static final int MAX_LENGTH = 20;

    private static final String INVALID_MESSAGE_KEY = "user.phone.number.invalid";

    private static final Pattern E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$");

    public PhoneNumber {
        if (value == null) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH || !E164_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
    }

    /**
     * Builds a phone number from an optional raw value.
     *
     * @param value raw phone number, possibly {@code null} or blank
     * @return the phone number, or {@code null} when no value was provided
     */
    public static PhoneNumber fromNullable(String value) {
        return value == null || value.isBlank() ? null : new PhoneNumber(value);
    }
}
