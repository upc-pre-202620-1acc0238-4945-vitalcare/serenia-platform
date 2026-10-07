package com.serenia.platform.dailycheckin.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a daily check-in.
 *
 * @param value the UUID identity
 */
public record CheckInId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "check.in.id.null";

    public CheckInId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static CheckInId generate() {
        return new CheckInId(UUID.randomUUID());
    }
}
