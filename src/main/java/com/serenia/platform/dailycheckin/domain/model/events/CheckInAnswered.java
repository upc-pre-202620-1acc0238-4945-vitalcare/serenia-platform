package com.serenia.platform.dailycheckin.domain.model.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain event raised when the older adult answered the check-in. Consumed by Wellbeing Monitoring.
 *
 * @param checkInId the identifier of the check-in
 * @param olderAdultId the older adult
 * @param checkDate the date of the check-in, in the older adult's time zone
 * @param mood the reported mood level
 * @param positiveActivity the reported positive activity, possibly {@code null}
 * @param occurredAt the instant of the answer, in UTC
 */
public record CheckInAnswered(UUID checkInId, UUID olderAdultId, LocalDate checkDate, String mood, String positiveActivity, Instant occurredAt) {
}
