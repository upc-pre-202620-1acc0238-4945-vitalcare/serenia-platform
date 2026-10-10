package com.serenia.platform.dailycheckin.application.internal.eventhandlers;

import com.serenia.platform.dailycheckin.domain.model.commands.InitializeCheckInPreferencesCommand;
import com.serenia.platform.dailycheckin.domain.services.CheckInPreferencesCommandService;
import com.serenia.platform.iam.domain.model.events.OlderAdultRegistered;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Creates the check-in preferences of a newly registered older adult.
 *
 * <p>Runs synchronously inside the registration transaction: if the preferences cannot be
 * created, the registration rolls back.</p>
 */
// Explicit name avoids conflicts with handlers of the same name in sibling bounded contexts
@Service("dailyCheckInOlderAdultRegisteredEventHandler")
public class OlderAdultRegisteredEventHandler {

    private final CheckInPreferencesCommandService checkInPreferencesCommandService;

    public OlderAdultRegisteredEventHandler(CheckInPreferencesCommandService checkInPreferencesCommandService) {
        this.checkInPreferencesCommandService = checkInPreferencesCommandService;
    }

    @EventListener
    public void on(OlderAdultRegistered event) {
        var result = checkInPreferencesCommandService.handle(new InitializeCheckInPreferencesCommand(event.userId()));
        if (result.isFailure())
            throw new IllegalStateException("Could not create the check-in preferences of older adult " + event.userId());
    }
}
