package com.serenia.platform.dailycheckin.domain.model.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain event raised when the questions were resumed, by decision of the older adult or because a new day started after a paused one.
 *
 * @param olderAdultId the older adult
 * @param resumedDate the date from which the questions are active again
 * @param occurredAt the instant of the resumption, in UTC
 */
public record DailyCheckInResumed(UUID olderAdultId, LocalDate resumedDate, Instant occurredAt) {
}
