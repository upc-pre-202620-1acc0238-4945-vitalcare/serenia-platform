package com.serenia.platform.iam.application.internal.eventhandlers;

import com.serenia.platform.iam.domain.model.commands.RevokeSessionsCommand;
import com.serenia.platform.iam.domain.model.events.PasswordChanged;
import com.serenia.platform.iam.domain.services.SessionCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Reacts to {@link PasswordChanged} by revoking the other sessions of the user, so a
 * device that still holds the old password loses access.
 *
 * <p>Runs synchronously, inside the same transaction as the password change.</p>
 */
// Explicit name avoids conflicts with handlers of the same name in sibling bounded contexts
@Service("iamPasswordChangedEventHandler")
@Slf4j
public class PasswordChangedEventHandler {

    private final SessionCommandService sessionCommandService;

    public PasswordChangedEventHandler(SessionCommandService sessionCommandService) {
        this.sessionCommandService = sessionCommandService;
    }

    @EventListener
    public void on(PasswordChanged event) {
        var command = new RevokeSessionsCommand(event.userId(), event.preservedSessionId());
        sessionCommandService.handle(command).toOptional().ifPresent(revokedSessions ->
                log.info("Revoked {} session(s) of user {} after password change", revokedSessions.size(), event.userId()));
    }
}
