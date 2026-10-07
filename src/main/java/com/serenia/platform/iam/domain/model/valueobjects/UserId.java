package com.serenia.platform.iam.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a user account.
 *
 * @param value the UUID that identifies the account
 */
public record UserId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "user.id.null";

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random user identity. */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}
