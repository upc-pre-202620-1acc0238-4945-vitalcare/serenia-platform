package com.serenia.platform.iam.domain.model.valueobjects;

/**
 * Description of the device from which a session was opened.
 *
 * @param value the device description
 */
public record DeviceInfo(String value) {
    public static final int MAX_LENGTH = 200;

    private static final String NOT_BLANK_MESSAGE_KEY = "session.device.info.blank";
    private static final String TOO_LONG_MESSAGE_KEY = "session.device.info.too.long";

    public DeviceInfo {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_MESSAGE_KEY);
        }
    }

    /**
     * Builds a device description from an optional raw value.
     *
     * @param value raw device description, possibly {@code null} or blank
     * @return the device description, or {@code null} when no value was provided
     */
    public static DeviceInfo fromNullable(String value) {
        return value == null || value.isBlank() ? null : new DeviceInfo(value);
    }
}
