package com.serenia.platform.dailycheckin.domain.model.commands;

import java.util.UUID;

/**
 * Intention of disabling the simplified mode of the interface.
 */
public record DisableSimplifiedModeCommand(UUID olderAdultId) {
}
