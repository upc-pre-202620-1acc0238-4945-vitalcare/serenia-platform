package com.serenia.platform.wellbeingmonitoring.domain.exceptions;

/**
 * Raised when a check-in already produced a small win; thrown by the persistence adapter on the unique index of the check-in.
 */
public class SmallWinAlreadyRecordedException extends WellbeingMonitoringDomainException {
    public SmallWinAlreadyRecordedException() {
        super("small.win.already.recorded");
    }
}
