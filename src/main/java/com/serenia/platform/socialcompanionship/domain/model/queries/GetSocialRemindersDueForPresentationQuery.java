package com.serenia.platform.socialcompanionship.domain.model.queries;

import java.time.Instant;

/**
 * Query of the scheduled or postponed reminders whose time arrived.
 */
public record GetSocialRemindersDueForPresentationQuery(Instant referenceTime) {
}
