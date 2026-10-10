package com.serenia.platform.dailycheckin.domain.exceptions;

/**
 * Raised when the older adult was already told that the check-in is available.
 */
public class CheckInAlreadyPromptedException extends DailyCheckInDomainException {
    public CheckInAlreadyPromptedException() {
        super("check.in.already.prompted");
    }
}
