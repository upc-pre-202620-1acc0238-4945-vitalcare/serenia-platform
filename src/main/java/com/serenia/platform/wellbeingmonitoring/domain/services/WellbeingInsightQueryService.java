package com.serenia.platform.wellbeingmonitoring.domain.services;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.SmallWin;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingSuggestion;
import com.serenia.platform.wellbeingmonitoring.domain.model.queries.GetActiveWellbeingSuggestionsQuery;
import com.serenia.platform.wellbeingmonitoring.domain.model.queries.GetSmallWinsByPeriodQuery;

import java.util.List;

/**
 * Contract of the read operations of the Wellbeing Monitoring context.
 */
public interface WellbeingInsightQueryService {

    Result<List<WellbeingSuggestion>, ApplicationError> handle(GetActiveWellbeingSuggestionsQuery query);

    Result<List<SmallWin>, ApplicationError> handle(GetSmallWinsByPeriodQuery query);
}
