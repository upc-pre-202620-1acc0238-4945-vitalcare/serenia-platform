package com.serenia.platform.socialcompanionship.domain.exceptions;

/**
 * Raised when sharing an audio while the older adult has no linked relatives.
 */
public class MessageWithoutRecipientsException extends SocialCompanionshipDomainException {
    public MessageWithoutRecipientsException() {
        super("companion.message.no.recipients");
    }
}
