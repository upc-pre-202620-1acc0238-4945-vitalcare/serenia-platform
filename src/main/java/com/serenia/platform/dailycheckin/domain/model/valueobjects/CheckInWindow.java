package com.serenia.platform.dailycheckin.domain.model.valueobjects;

import java.time.Instant;

/**
 * Window of a check-in: when it is presented and when it is due, both in UTC.
 *
 * @param scheduledFor the instant the question is presented
 * @param deadlineAt   the instant the check-in can no longer be answered; after {@code scheduledFor}
 */
public record CheckInWindow(Instant scheduledFor, Instant deadlineAt) {
    private static final String REQUIRED_MESSAGE_KEY = "check.in.window.required";
    private static final String INVALID_MESSAGE_KEY = "check.in.window.invalid";

    public CheckInWindow {
        if (scheduledFor == null || deadlineAt == null) {
            throw new IllegalArgumentException(REQUIRED_MESSAGE_KEY);
        }
        if (!deadlineAt.isAfter(scheduledFor)) {
            throw new IllegalArgumentException(INVALID_MESSAGE_KEY);
        }
    }

    /** Returns a window with the same presentation instant and a new deadline. */
    public CheckInWindow withDeadline(Instant newDeadlineAt) {
        return new CheckInWindow(scheduledFor, newDeadlineAt);
    }
}
