package com.serenia.platform.socialcompanionship.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a social reminder.
 *
 * @param value the UUID identity
 */
public record SocialReminderId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "social.reminder.id.null";

    public SocialReminderId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static SocialReminderId generate() {
        return new SocialReminderId(UUID.randomUUID());
    }
}
