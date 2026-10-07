package com.serenia.platform.socialcompanionship.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the older adult postponed a reminder until later the same day.
 *
 * @param socialReminderId the identifier of the reminder
 * @param olderAdultId the older adult
 * @param remindAt the new instant it must be presented, in UTC
 * @param occurredAt the instant of the postponement, in UTC
 */
public record SocialReminderPostponed(UUID socialReminderId, UUID olderAdultId, Instant remindAt, Instant occurredAt) {
}
