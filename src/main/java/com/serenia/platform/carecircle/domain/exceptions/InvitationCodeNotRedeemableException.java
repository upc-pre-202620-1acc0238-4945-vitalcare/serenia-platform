package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when an invitation code was already used or its validity ended.
 */
public class InvitationCodeNotRedeemableException extends CareCircleDomainException {
    public InvitationCodeNotRedeemableException() {
        super("invitation.code.not.redeemable");
    }
}
