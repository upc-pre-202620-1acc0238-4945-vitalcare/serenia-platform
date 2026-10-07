package com.serenia.platform.carecircle.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a care shift.
 *
 * @param value the UUID identity
 */
public record CareShiftId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "care.shift.id.null";

    public CareShiftId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static CareShiftId generate() {
        return new CareShiftId(UUID.randomUUID());
    }
}
