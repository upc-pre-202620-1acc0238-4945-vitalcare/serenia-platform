package com.serenia.platform.alertsandsafety.domain.services;

import com.serenia.platform.alertsandsafety.domain.model.aggregates.InactivityAlert;
import com.serenia.platform.alertsandsafety.domain.model.commands.AcknowledgeInactivityAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.EvaluateInactivityCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.RaiseInactivityAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.ResolveInactivityAlertCommand;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

/**
 * Contract of the write operations on inactivity alerts.
 */
public interface InactivityAlertCommandService {

    /**
     * Evaluates a missed check-in.
     *
     * @return whether the check-in was considered inactivity to be alerted
     */
    Result<Boolean, ApplicationError> handle(EvaluateInactivityCommand command);

    Result<InactivityAlert, ApplicationError> handle(RaiseInactivityAlertCommand command);

    Result<InactivityAlert, ApplicationError> handle(AcknowledgeInactivityAlertCommand command);

    Result<InactivityAlert, ApplicationError> handle(ResolveInactivityAlertCommand command);
}
