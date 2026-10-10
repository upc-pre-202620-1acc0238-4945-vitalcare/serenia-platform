package com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a small win.
 *
 * @param value the UUID identity
 */
public record SmallWinId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "small.win.id.null";

    public SmallWinId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static SmallWinId generate() {
        return new SmallWinId(UUID.randomUUID());
    }
}
