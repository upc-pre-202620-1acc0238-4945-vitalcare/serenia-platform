package com.serenia.platform.dailycheckin.domain.model.valueobjects;

/**
 * Status of a check-in: pending, answered, missed, or skipped because the questions were paused.
 */
public enum CheckInStatus {
    PENDING,
    ANSWERED,
    MISSED,
    SKIPPED
}
