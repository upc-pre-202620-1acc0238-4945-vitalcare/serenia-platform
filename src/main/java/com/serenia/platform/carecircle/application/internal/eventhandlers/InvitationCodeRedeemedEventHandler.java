package com.serenia.platform.carecircle.application.internal.eventhandlers;

import com.serenia.platform.carecircle.domain.model.commands.EstablishFamilyLinkCommand;
import com.serenia.platform.carecircle.domain.model.events.InvitationCodeRedeemed;
import com.serenia.platform.carecircle.domain.services.CareCircleCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Establishes the family link after an invitation code has been redeemed.
 *
 * <p>Runs inside the redemption transaction, so consuming the code and creating the link
 * happen together: if the link cannot be established, the redemption rolls back.</p>
 */
@Service("careCircleInvitationCodeRedeemedEventHandler")
public class InvitationCodeRedeemedEventHandler {

    private final CareCircleCommandService careCircleCommandService;

    public InvitationCodeRedeemedEventHandler(CareCircleCommandService careCircleCommandService) {
        this.careCircleCommandService = careCircleCommandService;
    }

    @EventListener
    public void on(InvitationCodeRedeemed event) {
        var command = new EstablishFamilyLinkCommand(event.careCircleId(), event.relativeId(), event.relationshipLabel());
        var result = careCircleCommandService.handle(command);
        if (result.isFailure())
            throw new IllegalStateException("Could not establish the family link of relative " + event.relativeId());
    }
}
