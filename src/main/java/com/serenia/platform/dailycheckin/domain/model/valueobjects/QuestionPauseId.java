package com.serenia.platform.dailycheckin.domain.model.valueobjects;

import java.util.UUID;

/**
 * Immutable identity of a question pause.
 *
 * @param value the UUID identity
 */
public record QuestionPauseId(UUID value) {
    private static final String NOT_NULL_MESSAGE_KEY = "question.pause.id.null";

    public QuestionPauseId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
    }

    /** Generates a new random identity. */
    public static QuestionPauseId generate() {
        return new QuestionPauseId(UUID.randomUUID());
    }
}
