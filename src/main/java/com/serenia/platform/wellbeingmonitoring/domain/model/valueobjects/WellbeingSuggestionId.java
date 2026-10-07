package com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a wellbeing suggestion.
 *
 * @param value the UUID identity
 */
public record WellbeingSuggestionId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "wellbeing.suggestion.id.null";

    public WellbeingSuggestionId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static WellbeingSuggestionId generate() {
        return new WellbeingSuggestionId(UUID.randomUUID());
    }
}
