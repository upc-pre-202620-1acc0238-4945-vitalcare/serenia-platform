package com.serenia.platform.dailycheckin.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of the check-in preferences of an older adult.
 *
 * @param value the UUID identity
 */
public record CheckInPreferencesId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "check.in.preferences.id.null";

    public CheckInPreferencesId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static CheckInPreferencesId generate() {
        return new CheckInPreferencesId(UUID.randomUUID());
    }
}
