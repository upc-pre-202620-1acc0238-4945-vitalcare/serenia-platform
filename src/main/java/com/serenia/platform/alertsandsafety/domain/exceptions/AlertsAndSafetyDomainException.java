package com.serenia.platform.alertsandsafety.domain.exceptions;

/**
 * Base class of the business-rule violations raised by the Alerts and Safety model.
 *
 * <p>The message is an i18n key, so the application layer can translate each
 * violation into an application error without parsing text.</p>
 */
public abstract class AlertsAndSafetyDomainException extends RuntimeException {
    protected AlertsAndSafetyDomainException(String messageKey) {
        super(messageKey);
    }
}
