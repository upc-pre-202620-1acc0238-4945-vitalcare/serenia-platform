package com.serenia.platform.socialcompanionship.domain.exceptions;

/**
 * Raised when someone who is not a recipient tries to play or view a message.
 */
public class NotMessageRecipientException extends SocialCompanionshipDomainException {
    public NotMessageRecipientException() {
        super("companion.message.not.recipient");
    }
}
