package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the day of the reminder ended without it being completed.
 *
 * @param socialReminderId the identifier of the reminder
 * @param olderAdultId the older adult
 * @param occurredAt the instant it was closed, in UTC
 */
public record SocialReminderMarkedAsMissed(UUID socialReminderId, UUID olderAdultId, Instant occurredAt) {
}
