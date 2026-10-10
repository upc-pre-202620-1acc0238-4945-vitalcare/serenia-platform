package com.serenia.platform.dailycheckin.domain.exceptions;

/**
 * Raised when the questions of the day are already paused.
 */
public class DailyPauseAlreadyActiveException extends DailyCheckInDomainException {
    public DailyPauseAlreadyActiveException() {
        super("check.in.daily.pause.already.active");
    }
}
