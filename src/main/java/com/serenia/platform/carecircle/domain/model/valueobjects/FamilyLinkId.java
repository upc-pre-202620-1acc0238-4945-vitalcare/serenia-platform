package com.serenia.platform.carecircle.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a family link.
 *
 * @param value the UUID identity
 */
public record FamilyLinkId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "family.link.id.null";

    public FamilyLinkId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static FamilyLinkId generate() {
        return new FamilyLinkId(UUID.randomUUID());
    }
}
