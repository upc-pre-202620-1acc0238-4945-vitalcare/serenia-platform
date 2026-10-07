package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when a family link does not belong to the care circle.
 */
public class FamilyLinkNotFoundException extends CareCircleDomainException {
    public FamilyLinkNotFoundException() {
        super("family.link.not.found");
    }
}
