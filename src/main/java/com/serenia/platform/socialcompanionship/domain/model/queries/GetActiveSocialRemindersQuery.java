package com.serenia.platform.socialcompanionship.domain.model.queries;

import java.util.UUID;

/**
 * Query of the scheduled, presented or postponed reminders of an older adult.
 */
public record GetActiveSocialRemindersQuery(UUID olderAdultId, UUID requesterId) {
}
