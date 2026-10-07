package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the older adult said they did the activity of the reminder.
 *
 * @param socialReminderId the identifier of the reminder
 * @param olderAdultId the older adult
 * @param occurredAt the instant of the completion, in UTC
 */
public record SocialReminderCompleted(UUID socialReminderId, UUID olderAdultId, Instant occurredAt) {
}
