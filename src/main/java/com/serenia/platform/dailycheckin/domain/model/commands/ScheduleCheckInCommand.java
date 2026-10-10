package com.serenia.platform.dailycheckin.domain.model.commands;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Intention of changing the local time of the daily check-in.
 */
public record ScheduleCheckInCommand(UUID olderAdultId, LocalTime reminderTime) {
}
