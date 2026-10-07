package com.serenia.platform.dailycheckin.domain.exceptions;

/**
 * Base class of the business-rule violations raised by the Daily Check-in model.
 *
 * <p>The message is an i18n key, so the application layer can translate each
 * violation into an application error without parsing text.</p>
 */
public abstract class DailyCheckInDomainException extends RuntimeException {
    protected DailyCheckInDomainException(String messageKey) {
        super(messageKey);
    }
}
