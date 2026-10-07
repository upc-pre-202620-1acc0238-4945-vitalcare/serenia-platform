package com.serenia.platform.socialcompanionship.domain.exceptions;

/**
 * Raised when postponing a reminder would move it past the end of the local day.
 */
public class NoTimeLeftTodayException extends SocialCompanionshipDomainException {
    public NoTimeLeftTodayException() {
        super("social.reminder.no.time.left.today");
    }
}
