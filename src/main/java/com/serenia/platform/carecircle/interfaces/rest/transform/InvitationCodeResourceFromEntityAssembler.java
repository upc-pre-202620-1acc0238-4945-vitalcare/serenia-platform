package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.entities.InvitationCode;
import com.serenia.platform.carecircle.interfaces.rest.resources.InvitationCodeResource;

/** Converts an {@link InvitationCode} entity into its {@link InvitationCodeResource} REST representation. */
public class InvitationCodeResourceFromEntityAssembler {
    public static InvitationCodeResource toResourceFromEntity(InvitationCode invitationCode) {
        return new InvitationCodeResource(
                invitationCode.getId().value(),
                invitationCode.getCode().value(),
                invitationCode.getStatus().name(),
                invitationCode.getExpiresAt());
    }
}
