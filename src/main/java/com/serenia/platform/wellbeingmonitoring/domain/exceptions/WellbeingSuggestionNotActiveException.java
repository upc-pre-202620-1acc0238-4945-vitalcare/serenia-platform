package com.serenia.platform.wellbeingmonitoring.domain.exceptions;

/**
 * Raised when a suggestion is not active anymore, or is not one of the active suggestions of the older adult.
 */
public class WellbeingSuggestionNotActiveException extends WellbeingMonitoringDomainException {
    public WellbeingSuggestionNotActiveException() {
        super("wellbeing.suggestion.not.active");
    }
}
