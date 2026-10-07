package com.serenia.platform.dailycheckin.domain.exceptions;

/**
 * Raised when the new deadline of a check-in is not after the current one.
 */
public class InvalidDeadlineExtensionException extends DailyCheckInDomainException {
    public InvalidDeadlineExtensionException() {
        super("check.in.deadline.extension.invalid");
    }
}
