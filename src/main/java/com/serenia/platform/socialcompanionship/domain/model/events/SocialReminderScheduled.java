package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a social reminder was scheduled.
 *
 * @param socialReminderId the identifier of the reminder
 * @param olderAdultId the older adult
 * @param remindAt the instant it must be presented, in UTC
 * @param occurredAt the instant of the scheduling, in UTC
 */
public record SocialReminderScheduled(UUID socialReminderId, UUID olderAdultId, Instant remindAt, Instant occurredAt) {
}
