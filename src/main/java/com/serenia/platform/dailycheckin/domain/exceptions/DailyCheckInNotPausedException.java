package com.serenia.platform.dailycheckin.domain.exceptions;

/**
 * Raised when resuming the questions although neither today nor the previous day was paused.
 */
public class DailyCheckInNotPausedException extends DailyCheckInDomainException {
    public DailyCheckInNotPausedException() {
        super("check.in.daily.pause.not.active");
    }
}
