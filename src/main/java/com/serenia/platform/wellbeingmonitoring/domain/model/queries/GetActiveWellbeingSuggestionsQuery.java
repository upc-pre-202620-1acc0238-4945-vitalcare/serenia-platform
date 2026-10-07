package com.serenia.platform.wellbeingmonitoring.domain.model.queries;

import java.util.UUID;

/**
 * Query of the active suggestions of an older adult, on behalf of the requester.
 */
public record GetActiveWellbeingSuggestionsQuery(UUID olderAdultId, UUID requesterId) {
}
