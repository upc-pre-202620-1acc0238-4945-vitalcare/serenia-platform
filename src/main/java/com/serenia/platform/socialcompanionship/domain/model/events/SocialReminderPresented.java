package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a reminder was presented to the older adult.
 *
 * @param socialReminderId the identifier of the reminder
 * @param olderAdultId the older adult
 * @param occurredAt the instant of the presentation, in UTC
 */
public record SocialReminderPresented(UUID socialReminderId, UUID olderAdultId, Instant occurredAt) {
}
