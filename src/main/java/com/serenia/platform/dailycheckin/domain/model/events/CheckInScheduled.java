package com.serenia.platform.dailycheckin.domain.model.events;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Domain event raised when the time of the daily check-in has changed; it applies from the next check-in.
 *
 * @param olderAdultId the older adult
 * @param reminderTime the new local reminder time
 * @param occurredAt the instant of the change, in UTC
 */
public record CheckInScheduled(UUID olderAdultId, LocalTime reminderTime, Instant occurredAt) {
}
