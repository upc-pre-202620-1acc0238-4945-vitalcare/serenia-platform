package com.serenia.platform.dailycheckin.domain.model.commands;

import java.util.UUID;

/**
 * Intention of creating the check-in preferences of a newly registered older adult.
 */
public record InitializeCheckInPreferencesCommand(UUID olderAdultId) {
}
