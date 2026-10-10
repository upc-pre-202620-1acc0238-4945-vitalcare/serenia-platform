package com.serenia.platform.dailycheckin.domain.model.valueobjects;

import java.time.Duration;

/**
 * Time the older adult has to answer, counted from the reminder time.
 *
 * @param minutes the time limit in minutes; must be greater than zero
 */
public record TimeLimit(int minutes) {
    public static final TimeLimit DEFAULT = new TimeLimit(180);

    private static final String NOT_POSITIVE_MESSAGE_KEY = "check.in.time.limit.not.positive";

    public TimeLimit {
        if (minutes <= 0) {
            throw new IllegalArgumentException(NOT_POSITIVE_MESSAGE_KEY);
        }
    }

    public Duration toDuration() {
        return Duration.ofMinutes(minutes);
    }
}
