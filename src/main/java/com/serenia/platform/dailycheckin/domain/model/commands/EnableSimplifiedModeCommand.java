package com.serenia.platform.dailycheckin.domain.model.commands;

import java.util.UUID;

/**
 * Intention of enabling the simplified mode of the interface.
 */
public record EnableSimplifiedModeCommand(UUID olderAdultId) {
}
