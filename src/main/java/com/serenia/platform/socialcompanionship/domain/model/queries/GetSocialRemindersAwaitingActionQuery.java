package com.serenia.platform.socialcompanionship.domain.model.queries;

import java.time.Instant;

/**
 * Query of the presented or postponed reminders before the given instant, candidates to be closed as not completed.
 */
public record GetSocialRemindersAwaitingActionQuery(Instant referenceTime) {
}
