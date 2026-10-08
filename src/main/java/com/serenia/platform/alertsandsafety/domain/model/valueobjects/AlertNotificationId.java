package com.serenia.platform.alertsandsafety.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of the notification of an emergency to a relative.
 *
 * @param value the UUID identity
 */
public record AlertNotificationId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "alert.notification.id.null";

    public AlertNotificationId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static AlertNotificationId generate() {
        return new AlertNotificationId(UUID.randomUUID());
    }
}
