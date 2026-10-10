package com.serenia.platform.dailycheckin.domain.exceptions;

/**
 * Raised when an operation requires a pending check-in but it was already answered, missed or skipped.
 */
public class CheckInNotPendingException extends DailyCheckInDomainException {
    public CheckInNotPendingException() {
        super("check.in.not.pending");
    }
}
