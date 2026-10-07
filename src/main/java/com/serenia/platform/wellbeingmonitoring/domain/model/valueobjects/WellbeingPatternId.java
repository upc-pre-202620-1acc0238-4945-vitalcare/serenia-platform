package com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a wellbeing pattern.
 *
 * @param value the UUID identity
 */
public record WellbeingPatternId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "wellbeing.pattern.id.null";

    public WellbeingPatternId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static WellbeingPatternId generate() {
        return new WellbeingPatternId(UUID.randomUUID());
    }
}
