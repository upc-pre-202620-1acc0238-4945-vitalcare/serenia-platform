package com.serenia.platform.wellbeingmonitoring.application.internal.eventhandlers;

import com.serenia.platform.dailycheckin.domain.model.events.CheckInAnswered;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.EvaluateWellbeingPatternCommand;
import com.serenia.platform.wellbeingmonitoring.domain.services.WellbeingInsightCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Starts the wellbeing evaluation when the older adult answers the check-in.
 *
 * <p>Runs after the answer is committed (or right away if it was saved outside a transaction),
 * and the evaluation runs in its own transaction. Failures are logged and never propagated,
 * so they cannot prevent the check-in from being recorded.</p>
 */
@Service("wellbeingMonitoringCheckInAnsweredEventHandler")
@Slf4j
public class CheckInAnsweredEventHandler {

    private final WellbeingInsightCommandService wellbeingInsightCommandService;

    public CheckInAnsweredEventHandler(WellbeingInsightCommandService wellbeingInsightCommandService) {
        this.wellbeingInsightCommandService = wellbeingInsightCommandService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(CheckInAnswered event) {
        var command = new EvaluateWellbeingPatternCommand(
                event.olderAdultId(), event.checkInId(), event.checkDate(), event.mood(), event.positiveActivity());
        try {
            var result = wellbeingInsightCommandService.handle(command);
            if (result.isFailure())
                log.warn("Wellbeing evaluation of check-in {} failed: {}", event.checkInId(), result);
        } catch (RuntimeException e) {
            log.error("Wellbeing evaluation of check-in {} failed", event.checkInId(), e);
        }
    }
}
