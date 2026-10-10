package com.serenia.platform.alertsandsafety.domain.services;

import com.serenia.platform.alertsandsafety.domain.model.aggregates.Alert;
import com.serenia.platform.alertsandsafety.domain.model.queries.GetAlertByIdQuery;
import com.serenia.platform.alertsandsafety.domain.model.queries.GetAlertsByOlderAdultIdQuery;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

import java.util.List;

/**
 * Contract of the read operations on alerts of both types.
 */
public interface AlertQueryService {

    /** Returns the alerts of the older adult, of both types, from the most recent to the oldest. */
    Result<List<Alert>, ApplicationError> handle(GetAlertsByOlderAdultIdQuery query);

    Result<Alert, ApplicationError> handle(GetAlertByIdQuery query);
}
