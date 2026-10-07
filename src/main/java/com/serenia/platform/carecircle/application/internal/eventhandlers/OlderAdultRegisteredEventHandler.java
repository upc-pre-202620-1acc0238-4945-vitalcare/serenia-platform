package com.serenia.platform.carecircle.application.internal.eventhandlers;

import com.serenia.platform.carecircle.domain.model.commands.CreateCareCircleCommand;
import com.serenia.platform.carecircle.domain.services.CareCircleCommandService;
import com.serenia.platform.iam.domain.model.events.OlderAdultRegistered;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Creates the care circle of a newly registered older adult.
 *
 * <p>Runs synchronously inside the registration transaction, so there is never an older
 * adult without a circle: if the circle cannot be created, the registration rolls back.</p>
 */
// Explicit name avoids conflicts with handlers of the same name in sibling bounded contexts
@Service("careCircleOlderAdultRegisteredEventHandler")
public class OlderAdultRegisteredEventHandler {

    private final CareCircleCommandService careCircleCommandService;

    public OlderAdultRegisteredEventHandler(CareCircleCommandService careCircleCommandService) {
        this.careCircleCommandService = careCircleCommandService;
    }

    @EventListener
    public void on(OlderAdultRegistered event) {
        var result = careCircleCommandService.handle(new CreateCareCircleCommand(event.userId()));
        if (result.isFailure())
            throw new IllegalStateException("Could not create the care circle of older adult " + event.userId());
    }
}
