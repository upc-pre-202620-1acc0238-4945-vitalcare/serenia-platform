package com.serenia.platform.carecircle.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a care circle.
 *
 * @param value the UUID identity
 */
public record CareCircleId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "care.circle.id.null";

    public CareCircleId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static CareCircleId generate() {
        return new CareCircleId(UUID.randomUUID());
    }
}
