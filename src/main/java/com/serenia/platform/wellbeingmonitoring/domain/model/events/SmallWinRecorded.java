package com.serenia.platform.wellbeingmonitoring.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a small win was recorded.
 *
 * @param olderAdultId the older adult
 * @param smallWinId the identifier of the small win
 * @param checkInId the check-in it comes from
 * @param occurredAt the instant of the record, in UTC
 */
public record SmallWinRecorded(UUID olderAdultId, UUID smallWinId, UUID checkInId, Instant occurredAt) {
}
