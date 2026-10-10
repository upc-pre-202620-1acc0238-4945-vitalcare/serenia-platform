package com.serenia.platform.dailycheckin.domain.model.queries;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Query of the check-ins of a date range, ordered by date.
 */
public record GetCheckInHistoryQuery(UUID olderAdultId, UUID requesterId, LocalDate fromDate, LocalDate toDate) {
    private static final String INVALID_RANGE_MESSAGE_KEY = "check.in.date.range.invalid";

    public GetCheckInHistoryQuery {
        if (fromDate == null || toDate == null || toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException(INVALID_RANGE_MESSAGE_KEY);
        }
    }
}
