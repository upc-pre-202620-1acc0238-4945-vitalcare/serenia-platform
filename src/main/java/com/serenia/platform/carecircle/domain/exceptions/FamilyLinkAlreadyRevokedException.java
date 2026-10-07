package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when trying to revoke a family link that is no longer active.
 */
public class FamilyLinkAlreadyRevokedException extends CareCircleDomainException {
    public FamilyLinkAlreadyRevokedException() {
        super("family.link.already.revoked");
    }
}
