package com.serenia.platform.alertsandsafety.application.internal.eventhandlers;

import com.serenia.platform.alertsandsafety.domain.model.commands.DispatchEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.events.EmergencyAlertRaised;
import com.serenia.platform.alertsandsafety.domain.services.EmergencyAlertCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Dispatches the emergency to the relatives.
 *
 * <p>Runs in the same thread but in its own transaction, after the alert is recorded: the
 * emergency stays recorded even if the dispatch fails, and the answer to the older adult already
 * reflects the outcome.</p>
 */
@Service("alertsAndSafetyEmergencyAlertRaisedEventHandler")
@Slf4j
public class EmergencyAlertRaisedEventHandler {

    private final EmergencyAlertCommandService emergencyAlertCommandService;

    public EmergencyAlertRaisedEventHandler(EmergencyAlertCommandService emergencyAlertCommandService) {
        this.emergencyAlertCommandService = emergencyAlertCommandService;
    }

    @EventListener
    public void on(EmergencyAlertRaised event) {
        try {
            var result = emergencyAlertCommandService.handle(new DispatchEmergencyAlertCommand(event.alertId()));
            if (result.isFailure())
                log.error("Dispatch of emergency alert {} failed: {}", event.alertId(), result);
        } catch (RuntimeException e) {
            log.error("Dispatch of emergency alert {} failed", event.alertId(), e);
        }
    }
}
