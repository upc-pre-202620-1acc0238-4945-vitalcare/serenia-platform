package com.serenia.platform.socialcompanionship.domain.exceptions;

/**
 * Raised when sharing or discarding a message that was already shared.
 */
public class MessageAlreadySharedException extends SocialCompanionshipDomainException {
    public MessageAlreadySharedException() {
        super("companion.message.already.shared");
    }
}
