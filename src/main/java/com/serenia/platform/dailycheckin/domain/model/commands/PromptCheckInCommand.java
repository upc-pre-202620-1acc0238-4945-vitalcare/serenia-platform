package com.serenia.platform.dailycheckin.domain.model.commands;

import java.util.UUID;

/**
 * Intention of telling the older adult that the check-in is still pending.
 */
public record PromptCheckInCommand(UUID checkInId) {
}
