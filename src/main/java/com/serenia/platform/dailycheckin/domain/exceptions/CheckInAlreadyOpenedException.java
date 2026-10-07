package com.serenia.platform.dailycheckin.domain.exceptions;

/**
 * Raised when the check-in of a date already exists; thrown by the persistence adapter when two runs open the same day at the same time.
 */
public class CheckInAlreadyOpenedException extends DailyCheckInDomainException {
    public CheckInAlreadyOpenedException() {
        super("check.in.already.opened");
    }
}
