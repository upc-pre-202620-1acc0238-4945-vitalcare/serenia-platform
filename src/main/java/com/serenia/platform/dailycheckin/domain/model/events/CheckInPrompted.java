package com.serenia.platform.dailycheckin.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the older adult has been told, inside the application, that the check-in is available.
 *
 * @param checkInId the identifier of the check-in
 * @param olderAdultId the older adult
 * @param occurredAt the instant of the prompt, in UTC
 */
public record CheckInPrompted(UUID checkInId, UUID olderAdultId, Instant occurredAt) {
}
