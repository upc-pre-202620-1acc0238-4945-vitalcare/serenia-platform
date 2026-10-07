package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Base class of the business-rule violations raised by the Care Circle model.
 *
 * <p>The message is an i18n key, so the application layer can translate each
 * violation into an application error without parsing text.</p>
 */
public abstract class CareCircleDomainException extends RuntimeException {
    protected CareCircleDomainException(String messageKey) {
        super(messageKey);
    }
}
