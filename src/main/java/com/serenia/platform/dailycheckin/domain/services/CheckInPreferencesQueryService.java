package com.serenia.platform.dailycheckin.domain.services;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences;
import com.serenia.platform.dailycheckin.domain.model.queries.GetAllCheckInPreferencesQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInPreferencesByOlderAdultIdQuery;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

/**
 * Contract of the read operations on the check-in preferences.
 */
public interface CheckInPreferencesQueryService {

    /**
     * Returns the preferences of the older adult.
     *
     * @return the preferences and whether the questions are paused on the older adult's current day
     */
    Result<ImmutablePair<CheckInPreferences, Boolean>, ApplicationError> handle(GetCheckInPreferencesByOlderAdultIdQuery query);

    List<CheckInPreferences> handle(GetAllCheckInPreferencesQuery query);
}
