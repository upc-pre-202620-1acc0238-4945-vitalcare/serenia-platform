package com.serenia.platform.dailycheckin.domain.model.valueobjects;

import java.time.LocalTime;

/**
 * Local time at which the question of the day is presented.
 *
 * <p>Must be between 06:00 and 20:00; with the default time limit, the latest possible
 * check-in is due at 23:00 of the same day.</p>
 *
 * @param value the local time, truncated to minutes
 */
public record ReminderTime(LocalTime value) {
    public static final LocalTime EARLIEST = LocalTime.of(6, 0);
    public static final LocalTime LATEST = LocalTime.of(20, 0);
    public static final ReminderTime DEFAULT = new ReminderTime(LocalTime.of(10, 0));

    private static final String OUT_OF_RANGE_MESSAGE_KEY = "check.in.reminder.time.out.of.range";

    public ReminderTime {
        if (value == null) {
            throw new IllegalArgumentException(OUT_OF_RANGE_MESSAGE_KEY);
        }
        value = value.withSecond(0).withNano(0);
        if (value.isBefore(EARLIEST) || value.isAfter(LATEST)) {
            throw new IllegalArgumentException(OUT_OF_RANGE_MESSAGE_KEY);
        }
    }
}
