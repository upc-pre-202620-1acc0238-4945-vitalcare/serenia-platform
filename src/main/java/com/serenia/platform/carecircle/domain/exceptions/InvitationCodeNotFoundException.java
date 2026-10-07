package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when an invitation code does not belong to the care circle.
 */
public class InvitationCodeNotFoundException extends CareCircleDomainException {
    public InvitationCodeNotFoundException() {
        super("invitation.code.not.found");
    }
}
