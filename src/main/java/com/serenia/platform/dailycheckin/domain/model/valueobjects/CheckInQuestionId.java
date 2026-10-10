package com.serenia.platform.dailycheckin.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a question of the check-in catalog.
 *
 * @param value the UUID identity
 */
public record CheckInQuestionId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "check.in.question.id.null";

    public CheckInQuestionId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static CheckInQuestionId generate() {
        return new CheckInQuestionId(UUID.randomUUID());
    }
}
