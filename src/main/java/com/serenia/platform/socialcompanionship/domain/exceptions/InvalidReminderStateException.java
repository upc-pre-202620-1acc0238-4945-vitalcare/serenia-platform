package com.serenia.platform.socialcompanionship.domain.exceptions;

/**
 * Raised when the status of a social reminder does not allow the requested action.
 */
public class InvalidReminderStateException extends SocialCompanionshipDomainException {
    public InvalidReminderStateException() {
        super("social.reminder.invalid.state");
    }
}
