package com.serenia.platform.alertsandsafety.domain.model.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain event raised when a missed check-in is inactivity that must be alerted.
 *
 * @param olderAdultId the older adult who did not answer
 * @param checkInId the missed check-in
 * @param checkDate the date of the check-in, in the older adult's time zone
 * @param occurredAt the instant of the evaluation, in UTC
 */
public record InactivityDetected(UUID olderAdultId, UUID checkInId, LocalDate checkDate, Instant occurredAt) {
}
