package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the older adult canceled a reminder.
 *
 * @param socialReminderId the identifier of the reminder
 * @param olderAdultId the older adult
 * @param occurredAt the instant of the cancellation, in UTC
 */
public record SocialReminderCanceled(UUID socialReminderId, UUID olderAdultId, Instant occurredAt) {
}
