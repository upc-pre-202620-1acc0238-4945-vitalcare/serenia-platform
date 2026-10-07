package com.serenia.platform.dailycheckin.domain.services;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInHistoryQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInsDueForPromptQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInsPastDeadlineQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetTodayCheckInQuery;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

/**
 * Contract of the read operations on daily check-ins.
 */
public interface CheckInQueryService {

    /**
     * Returns the check-in of the older adult's current day.
     *
     * @return the check-in and whether the questions are paused today
     */
    Result<ImmutablePair<CheckIn, Boolean>, ApplicationError> handle(GetTodayCheckInQuery query);

    Result<List<CheckIn>, ApplicationError> handle(GetCheckInHistoryQuery query);

    List<CheckIn> handle(GetCheckInsDueForPromptQuery query);

    List<CheckIn> handle(GetCheckInsPastDeadlineQuery query);
}
