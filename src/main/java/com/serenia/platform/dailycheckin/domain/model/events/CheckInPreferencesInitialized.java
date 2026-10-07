package com.serenia.platform.dailycheckin.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the check-in preferences of an older adult have been created.
 *
 * @param olderAdultId the older adult the preferences belong to
 * @param occurredAt the instant of the creation, in UTC
 */
public record CheckInPreferencesInitialized(UUID olderAdultId, Instant occurredAt) {
}
