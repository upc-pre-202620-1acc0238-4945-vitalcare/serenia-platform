package com.serenia.platform.carecircle.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of an invitation code.
 *
 * @param value the UUID identity
 */
public record InvitationCodeId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "invitation.code.id.null";

    public InvitationCodeId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static InvitationCodeId generate() {
        return new InvitationCodeId(UUID.randomUUID());
    }
}
