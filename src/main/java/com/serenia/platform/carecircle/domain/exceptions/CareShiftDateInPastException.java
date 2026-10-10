package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when a care shift is assigned or reassigned for a date before the
 * older adult's current day.
 */
public class CareShiftDateInPastException extends CareCircleDomainException {
    public CareShiftDateInPastException() {
        super("care.shift.date.in.past");
    }
}
