package com.serenia.platform.dailycheckin.domain.model.queries;

import java.util.UUID;

/**
 * Query of the check-in of the current day and whether the questions are paused; used by the older adult to answer and by relatives to know the status.
 */
public record GetTodayCheckInQuery(UUID olderAdultId, UUID requesterId) {
}
