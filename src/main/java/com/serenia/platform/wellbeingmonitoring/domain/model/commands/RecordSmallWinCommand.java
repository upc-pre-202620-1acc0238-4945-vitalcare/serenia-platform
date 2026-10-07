package com.serenia.platform.wellbeingmonitoring.domain.model.commands;

import java.util.UUID;

/**
 * Intention of recording a small win from a positive day.
 */
public record RecordSmallWinCommand(UUID olderAdultId, UUID checkInId, String positiveActivity) {
}
