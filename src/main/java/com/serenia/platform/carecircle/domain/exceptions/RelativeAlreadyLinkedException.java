package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when a relative already has an active link with the care circle.
 */
public class RelativeAlreadyLinkedException extends CareCircleDomainException {
    public RelativeAlreadyLinkedException() {
        super("family.link.relative.already.linked");
    }
}
