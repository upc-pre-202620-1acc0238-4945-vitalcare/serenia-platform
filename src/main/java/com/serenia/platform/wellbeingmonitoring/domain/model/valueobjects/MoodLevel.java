package com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects;

/**
 * Mood reported in a check-in, translated from the values published by Daily Check-in.
 */
public enum MoodLevel {
    VERY_LOW,
    LOW,
    NEUTRAL,
    GOOD,
    VERY_GOOD;

    /** Indicates whether the mood reveals discomfort: low or very low. */
    public boolean isDiscomfort() {
        return this == VERY_LOW || this == LOW;
    }

    /** Indicates whether the mood is good or very good. */
    public boolean isPositive() {
        return this == GOOD || this == VERY_GOOD;
    }
}
