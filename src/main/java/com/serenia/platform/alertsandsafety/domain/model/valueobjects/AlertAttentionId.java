package com.serenia.platform.alertsandsafety.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of an action of a relative on an alert.
 *
 * @param value the UUID identity
 */
public record AlertAttentionId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "alert.attention.id.null";

    public AlertAttentionId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static AlertAttentionId generate() {
        return new AlertAttentionId(UUID.randomUUID());
    }
}
