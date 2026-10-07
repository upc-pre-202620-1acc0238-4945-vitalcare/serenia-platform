package com.serenia.platform.dailycheckin.domain.model.commands;

import java.util.UUID;

/**
 * Intention of pausing the questions of the current day.
 */
public record ActivateDailyPauseCommand(UUID olderAdultId) {
}
