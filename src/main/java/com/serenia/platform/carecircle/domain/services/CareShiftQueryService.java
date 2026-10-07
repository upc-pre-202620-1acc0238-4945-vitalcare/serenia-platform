package com.serenia.platform.carecircle.domain.services;

import com.serenia.platform.carecircle.domain.model.aggregates.CareShift;
import com.serenia.platform.carecircle.domain.model.queries.GetCareShiftsByDateRangeQuery;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

import java.util.List;

/**
 * Contract of the read operations on the care shift calendar.
 */
public interface CareShiftQueryService {

    Result<List<CareShift>, ApplicationError> handle(GetCareShiftsByDateRangeQuery query);
}
