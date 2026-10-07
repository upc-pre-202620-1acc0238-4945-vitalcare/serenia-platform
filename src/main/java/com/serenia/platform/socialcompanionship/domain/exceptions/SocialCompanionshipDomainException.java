package com.serenia.platform.socialcompanionship.domain.exceptions;

/**
 * Base class of the business-rule violations raised by the Social Companionship model.
 *
 * <p>The message is an i18n key, so the application layer can translate each
 * violation into an application error without parsing text.</p>
 */
public abstract class SocialCompanionshipDomainException extends RuntimeException {
    protected SocialCompanionshipDomainException(String messageKey) {
        super(messageKey);
    }
}
