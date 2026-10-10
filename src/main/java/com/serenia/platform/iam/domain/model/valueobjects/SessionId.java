package com.serenia.platform.iam.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a session.
 *
 * <p>It is generated before the session token is issued, because the token
 * carries the session identifier inside its claims.</p>
 *
 * @param value the UUID that identifies the session
 */
public record SessionId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "session.id.null";

    public SessionId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random session identity. */
    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }
}
