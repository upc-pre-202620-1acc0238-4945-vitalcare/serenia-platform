package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when the date of a care shift is already covered in the care circle.
 *
 * <p>Also thrown by the persistence adapter when two relatives take the same date
 * at the same time and collide on the unique index.</p>
 */
public class CareShiftDateAlreadyCoveredException extends CareCircleDomainException {
    public CareShiftDateAlreadyCoveredException() {
        super("care.shift.date.already.covered");
    }
}
