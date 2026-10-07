package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a photo message.
 *
 * @param value the UUID identity
 */
public record PhotoMessageId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "photo.message.id.null";

    public PhotoMessageId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static PhotoMessageId generate() {
        return new PhotoMessageId(UUID.randomUUID());
    }
}
