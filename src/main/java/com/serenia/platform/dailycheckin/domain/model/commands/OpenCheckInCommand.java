package com.serenia.platform.dailycheckin.domain.model.commands;

import java.util.UUID;

/**
 * Intention of opening the check-in of the older adult's current day; if it already exists, nothing changes.
 */
public record OpenCheckInCommand(UUID olderAdultId) {
}
