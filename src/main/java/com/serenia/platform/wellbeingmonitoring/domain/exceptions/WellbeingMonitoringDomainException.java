package com.serenia.platform.wellbeingmonitoring.domain.exceptions;

/**
 * Base class of the business-rule violations raised by the Wellbeing Monitoring model.
 *
 * <p>The message is an i18n key, so the application layer can translate each
 * violation into an application error without parsing text.</p>
 */
public abstract class WellbeingMonitoringDomainException extends RuntimeException {
    protected WellbeingMonitoringDomainException(String messageKey) {
        super(messageKey);
    }
}
