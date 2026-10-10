package com.serenia.platform.wellbeingmonitoring.application.internal.eventhandlers;

import com.serenia.platform.wellbeingmonitoring.domain.model.commands.RecordSmallWinCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.events.WellbeingTrendImproved;
import com.serenia.platform.wellbeingmonitoring.domain.services.WellbeingInsightCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Records the small win of a positive day, inside the transaction of the evaluation: if it
 * cannot be recorded, the evaluation rolls back.
 */
@Service("wellbeingMonitoringWellbeingTrendImprovedEventHandler")
public class WellbeingTrendImprovedEventHandler {

    private final WellbeingInsightCommandService wellbeingInsightCommandService;

    public WellbeingTrendImprovedEventHandler(WellbeingInsightCommandService wellbeingInsightCommandService) {
        this.wellbeingInsightCommandService = wellbeingInsightCommandService;
    }

    @EventListener
    public void on(WellbeingTrendImproved event) {
        var result = wellbeingInsightCommandService.handle(
                new RecordSmallWinCommand(event.olderAdultId(), event.checkInId(), event.positiveActivity()));
        if (result.isFailure())
            throw new IllegalStateException("Could not record the small win of check-in " + event.checkInId());
    }
}
