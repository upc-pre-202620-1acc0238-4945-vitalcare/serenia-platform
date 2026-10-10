package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of an audio message.
 *
 * @param value the UUID identity
 */
public record AudioMessageId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "audio.message.id.null";

    public AudioMessageId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static AudioMessageId generate() {
        return new AudioMessageId(UUID.randomUUID());
    }
}
