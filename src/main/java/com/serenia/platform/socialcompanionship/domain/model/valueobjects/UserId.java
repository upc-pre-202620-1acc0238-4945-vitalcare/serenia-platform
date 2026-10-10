package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

import java.util.UUID;

/**
 * Reference by identity to any account of Identity &amp; Access, used when the role does not matter, such as the recipient of a message.
 *
 * @param value the UUID identity
 */
public record UserId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "companion.user.id.null";

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }
}
