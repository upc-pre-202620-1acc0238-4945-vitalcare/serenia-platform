package com.serenia.platform.alertsandsafety.application.internal.eventhandlers;

import com.serenia.platform.alertsandsafety.domain.model.commands.EvaluateInactivityCommand;
import com.serenia.platform.alertsandsafety.domain.services.InactivityAlertCommandService;
import com.serenia.platform.dailycheckin.domain.model.events.CheckInMissed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Starts the inactivity evaluation when a check-in ends without an answer.
 *
 * <p>Runs after the closing of the check-in is committed (or right away if it was saved outside
 * a transaction), and the evaluation runs in its own transaction. Failures are logged and never
 * propagated, so they cannot affect Daily Check-in.</p>
 */
@Service("alertsAndSafetyCheckInMissedEventHandler")
@Slf4j
public class CheckInMissedEventHandler {

    private final InactivityAlertCommandService inactivityAlertCommandService;

    public CheckInMissedEventHandler(InactivityAlertCommandService inactivityAlertCommandService) {
        this.inactivityAlertCommandService = inactivityAlertCommandService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(CheckInMissed event) {
        var command = new EvaluateInactivityCommand(event.olderAdultId(), event.checkInId(), event.checkDate());
        try {
            var result = inactivityAlertCommandService.handle(command);
            if (result.isFailure())
                log.warn("Inactivity evaluation of check-in {} failed: {}", event.checkInId(), result);
        } catch (RuntimeException e) {
            log.error("Inactivity evaluation of check-in {} failed", event.checkInId(), e);
        }
    }
}
