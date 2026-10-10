package com.serenia.platform.wellbeingmonitoring.domain.model.queries;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Query of the small wins of a period, ordered by date; both dates are inclusive and
 * expressed in the older adult's time zone.
 */
public record GetSmallWinsByPeriodQuery(UUID olderAdultId, UUID requesterId, LocalDate fromDate, LocalDate toDate) {
    private static final String INVALID_RANGE_MESSAGE_KEY = "small.win.date.range.invalid";

    public GetSmallWinsByPeriodQuery {
        if (fromDate == null || toDate == null || toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException(INVALID_RANGE_MESSAGE_KEY);
        }
    }
}
