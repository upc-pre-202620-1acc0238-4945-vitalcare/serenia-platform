package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when a care shift is reassigned to the relative already in charge of it.
 */
public class CareShiftAlreadyAssignedToRelativeException extends CareCircleDomainException {
    public CareShiftAlreadyAssignedToRelativeException() {
        super("care.shift.already.assigned.to.relative");
    }
}
