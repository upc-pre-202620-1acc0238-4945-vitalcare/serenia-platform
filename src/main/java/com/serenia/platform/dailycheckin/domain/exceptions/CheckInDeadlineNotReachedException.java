package com.serenia.platform.dailycheckin.domain.exceptions;

/**
 * Raised when trying to mark as missed a check-in whose deadline has not passed yet.
 */
public class CheckInDeadlineNotReachedException extends DailyCheckInDomainException {
    public CheckInDeadlineNotReachedException() {
        super("check.in.deadline.not.reached");
    }
}
