package com.serenia.platform.wellbeingmonitoring.domain.model.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain event raised when the evaluated day showed positive wellbeing.
 *
 * @param olderAdultId the older adult
 * @param checkInId the evaluated check-in
 * @param checkDate the date of the check-in, in the older adult's time zone
 * @param positiveActivity the reported positive activity, possibly {@code null}
 * @param occurredAt the instant of the evaluation, in UTC
 */
public record WellbeingTrendImproved(UUID olderAdultId, UUID checkInId, LocalDate checkDate, String positiveActivity, Instant occurredAt) {
}
