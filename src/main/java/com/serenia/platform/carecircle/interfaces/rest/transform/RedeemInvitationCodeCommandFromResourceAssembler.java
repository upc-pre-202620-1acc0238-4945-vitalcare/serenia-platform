package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.commands.RedeemInvitationCodeCommand;
import com.serenia.platform.carecircle.interfaces.rest.resources.RedeemInvitationCodeResource;

import java.util.UUID;

/**
 * Combines a {@link RedeemInvitationCodeResource} with the authenticated relative into a
 * {@link RedeemInvitationCodeCommand}.
 */
public class RedeemInvitationCodeCommandFromResourceAssembler {
    public static RedeemInvitationCodeCommand toCommandFromResource(UUID relativeId, RedeemInvitationCodeResource resource) {
        return new RedeemInvitationCodeCommand(resource.code(), relativeId, resource.relationshipLabel());
    }
}
