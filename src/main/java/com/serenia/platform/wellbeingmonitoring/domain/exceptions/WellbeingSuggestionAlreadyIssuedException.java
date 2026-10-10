package com.serenia.platform.wellbeingmonitoring.domain.exceptions;

/**
 * Raised when a pattern already has an active suggestion.
 */
public class WellbeingSuggestionAlreadyIssuedException extends WellbeingMonitoringDomainException {
    public WellbeingSuggestionAlreadyIssuedException() {
        super("wellbeing.suggestion.already.issued");
    }
}
