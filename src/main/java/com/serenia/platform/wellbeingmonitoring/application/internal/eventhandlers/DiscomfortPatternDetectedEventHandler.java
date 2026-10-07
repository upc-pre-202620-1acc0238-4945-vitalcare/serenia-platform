package com.serenia.platform.wellbeingmonitoring.application.internal.eventhandlers;

import com.serenia.platform.wellbeingmonitoring.domain.model.commands.IssueWellbeingSuggestionCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.events.DiscomfortPatternDetected;
import com.serenia.platform.wellbeingmonitoring.domain.services.WellbeingInsightCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Issues the action suggestion of a detected pattern, inside the transaction of the evaluation:
 * if it cannot be issued, the evaluation rolls back.
 */
@Service("wellbeingMonitoringDiscomfortPatternDetectedEventHandler")
public class DiscomfortPatternDetectedEventHandler {

    private final WellbeingInsightCommandService wellbeingInsightCommandService;

    public DiscomfortPatternDetectedEventHandler(WellbeingInsightCommandService wellbeingInsightCommandService) {
        this.wellbeingInsightCommandService = wellbeingInsightCommandService;
    }

    @EventListener
    public void on(DiscomfortPatternDetected event) {
        var result = wellbeingInsightCommandService.handle(
                new IssueWellbeingSuggestionCommand(event.olderAdultId(), event.patternId()));
        if (result.isFailure())
            throw new IllegalStateException("Could not issue the suggestion of pattern " + event.patternId());
    }
}
