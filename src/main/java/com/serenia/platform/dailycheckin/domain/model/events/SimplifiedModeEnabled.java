package com.serenia.platform.dailycheckin.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the simplified mode of the interface has been enabled.
 *
 * @param olderAdultId the older adult
 * @param occurredAt the instant of the change, in UTC
 */
public record SimplifiedModeEnabled(UUID olderAdultId, Instant occurredAt) {
}
