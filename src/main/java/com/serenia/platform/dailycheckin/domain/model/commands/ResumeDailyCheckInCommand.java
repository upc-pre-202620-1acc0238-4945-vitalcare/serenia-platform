package com.serenia.platform.dailycheckin.domain.model.commands;

import java.util.UUID;

/**
 * Intention of resuming the questions, by decision of the older adult or because a new day starts after a paused one.
 */
public record ResumeDailyCheckInCommand(UUID olderAdultId) {
}
