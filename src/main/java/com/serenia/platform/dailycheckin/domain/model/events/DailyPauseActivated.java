package com.serenia.platform.dailycheckin.domain.model.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain event raised when the older adult paused the questions of the day.
 *
 * @param olderAdultId the older adult
 * @param pausedDate the paused date, in the older adult's time zone
 * @param occurredAt the instant the pause was activated, in UTC
 */
public record DailyPauseActivated(UUID olderAdultId, LocalDate pausedDate, Instant occurredAt) {
}
