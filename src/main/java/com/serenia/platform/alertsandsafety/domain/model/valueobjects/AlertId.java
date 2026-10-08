package com.serenia.platform.alertsandsafety.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of an alert of either type.
 *
 * @param value the UUID identity
 */
public record AlertId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "alert.id.null";

    public AlertId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static AlertId generate() {
        return new AlertId(UUID.randomUUID());
    }
}
