package com.serenia.platform.carecircle.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a shared note.
 *
 * @param value the UUID identity
 */
public record SharedNoteId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "shared.note.id.null";

    public SharedNoteId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static SharedNoteId generate() {
        return new SharedNoteId(UUID.randomUUID());
    }
}
