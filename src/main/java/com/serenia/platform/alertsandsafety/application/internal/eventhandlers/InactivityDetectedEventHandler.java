package com.serenia.platform.alertsandsafety.application.internal.eventhandlers;

import com.serenia.platform.alertsandsafety.domain.model.commands.RaiseInactivityAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.events.InactivityDetected;
import com.serenia.platform.alertsandsafety.domain.services.InactivityAlertCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Raises the inactivity alert inside the transaction of the evaluation: if it cannot be raised,
 * the evaluation rolls back.
 */
@Service("alertsAndSafetyInactivityDetectedEventHandler")
public class InactivityDetectedEventHandler {

    private final InactivityAlertCommandService inactivityAlertCommandService;

    public InactivityDetectedEventHandler(InactivityAlertCommandService inactivityAlertCommandService) {
        this.inactivityAlertCommandService = inactivityAlertCommandService;
    }

    @EventListener
    public void on(InactivityDetected event) {
        var result = inactivityAlertCommandService.handle(new RaiseInactivityAlertCommand(event.olderAdultId(), event.checkInId()));
        if (result.isFailure())
            throw new IllegalStateException("Could not raise the inactivity alert of check-in " + event.checkInId());
    }
}
