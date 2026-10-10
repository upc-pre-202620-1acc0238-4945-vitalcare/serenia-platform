package com.serenia.platform.wellbeingmonitoring.domain.model.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain event raised when a sustained discomfort was detected over several consecutive days.
 *
 * @param olderAdultId the older adult
 * @param patternId the identifier of the detected pattern
 * @param consecutiveDays the number of consecutive days with low mood
 * @param startDate the first day of the pattern, in the older adult's time zone
 * @param endDate the last day of the pattern, in the older adult's time zone
 * @param occurredAt the instant of the detection, in UTC
 */
public record DiscomfortPatternDetected(UUID olderAdultId, UUID patternId, int consecutiveDays, LocalDate startDate, LocalDate endDate, Instant occurredAt) {
}
