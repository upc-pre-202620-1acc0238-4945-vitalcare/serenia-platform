package com.serenia.platform.dailycheckin.domain.exceptions;

/**
 * Raised when the older adult answers a check-in whose deadline has already passed.
 */
public class CheckInDeadlinePassedException extends DailyCheckInDomainException {
    public CheckInDeadlinePassedException() {
        super("check.in.deadline.passed");
    }
}
