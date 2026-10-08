package com.serenia.platform.alertsandsafety.domain.services;

import com.serenia.platform.alertsandsafety.domain.model.aggregates.EmergencyAlert;
import com.serenia.platform.alertsandsafety.domain.model.commands.AcknowledgeEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.ConfirmEmergencyAlertDeliveryCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.DispatchEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.ResolveEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.TriggerEmergencyAlertCommand;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

/**
 * Contract of the write operations on emergency alerts.
 */
public interface EmergencyAlertCommandService {

    /**
     * Records the emergency and dispatches it to the relatives.
     *
     * @return the alert in its final state, which tells whether the family was notified
     */
    Result<EmergencyAlert, ApplicationError> handle(TriggerEmergencyAlertCommand command);

    Result<EmergencyAlert, ApplicationError> handle(DispatchEmergencyAlertCommand command);

    Result<EmergencyAlert, ApplicationError> handle(ConfirmEmergencyAlertDeliveryCommand command);

    Result<EmergencyAlert, ApplicationError> handle(AcknowledgeEmergencyAlertCommand command);

    Result<EmergencyAlert, ApplicationError> handle(ResolveEmergencyAlertCommand command);
}
