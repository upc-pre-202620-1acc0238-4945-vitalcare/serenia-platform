package com.serenia.platform.alertsandsafety.application.internal.eventhandlers;

import com.serenia.platform.alertsandsafety.domain.model.commands.ConfirmEmergencyAlertDeliveryCommand;
import com.serenia.platform.alertsandsafety.domain.model.events.EmergencyAlertDispatched;
import com.serenia.platform.alertsandsafety.domain.services.EmergencyAlertCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Confirms the delivery of the emergency inside the transaction of the dispatch: if it cannot be
 * confirmed, the dispatch rolls back.
 */
@Service("alertsAndSafetyEmergencyAlertDispatchedEventHandler")
public class EmergencyAlertDispatchedEventHandler {

    private final EmergencyAlertCommandService emergencyAlertCommandService;

    public EmergencyAlertDispatchedEventHandler(EmergencyAlertCommandService emergencyAlertCommandService) {
        this.emergencyAlertCommandService = emergencyAlertCommandService;
    }

    @EventListener
    public void on(EmergencyAlertDispatched event) {
        var result = emergencyAlertCommandService.handle(new ConfirmEmergencyAlertDeliveryCommand(event.alertId()));
        if (result.isFailure())
            throw new IllegalStateException("Could not confirm the delivery of emergency alert " + event.alertId());
    }
}
