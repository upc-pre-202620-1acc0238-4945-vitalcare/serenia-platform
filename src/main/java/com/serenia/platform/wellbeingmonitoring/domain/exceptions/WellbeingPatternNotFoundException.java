package com.serenia.platform.wellbeingmonitoring.domain.exceptions;

/**
 * Raised when a suggestion is requested for a pattern that is not the latest discomfort pattern.
 */
public class WellbeingPatternNotFoundException extends WellbeingMonitoringDomainException {
    public WellbeingPatternNotFoundException() {
        super("wellbeing.pattern.not.found");
    }
}
