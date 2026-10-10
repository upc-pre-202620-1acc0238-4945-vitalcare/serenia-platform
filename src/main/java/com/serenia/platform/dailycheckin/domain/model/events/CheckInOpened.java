package com.serenia.platform.dailycheckin.domain.model.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain event raised when the check-in of a day has been opened.
 *
 * @param checkInId the identifier of the check-in
 * @param olderAdultId the older adult
 * @param checkDate the date of the check-in, in the older adult's time zone
 * @param occurredAt the instant of the opening, in UTC
 */
public record CheckInOpened(UUID checkInId, UUID olderAdultId, LocalDate checkDate, Instant occurredAt) {
}
