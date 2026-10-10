package com.serenia.platform.dailycheckin.domain.model.queries;

import java.util.UUID;

/**
 * Query of the check-in preferences of an older adult, on behalf of the requester.
 */
public record GetCheckInPreferencesByOlderAdultIdQuery(UUID olderAdultId, UUID requesterId) {
}
