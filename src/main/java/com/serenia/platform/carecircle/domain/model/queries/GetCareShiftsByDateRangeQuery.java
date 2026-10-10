package com.serenia.platform.carecircle.domain.model.queries;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Query of the care shifts of a circle within a date range; with a single-day range it
 * returns the shift in charge of that day.
 */
public record GetCareShiftsByDateRangeQuery(UUID careCircleId, UUID requesterId, LocalDate fromDate, LocalDate toDate) {
    private static final String INVALID_RANGE_MESSAGE_KEY = "care.shift.date.range.invalid";

    public GetCareShiftsByDateRangeQuery {
        if (fromDate == null || toDate == null || toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException(INVALID_RANGE_MESSAGE_KEY);
        }
    }
}
