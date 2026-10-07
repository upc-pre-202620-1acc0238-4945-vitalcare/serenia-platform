package com.serenia.platform.carecircle.domain.model.commands;

import java.util.UUID;

/**
 * Intention of changing the relative in charge of an existing care shift.
 */
public record ReassignCareShiftCommand(UUID careShiftId, UUID newRelativeId, UUID requesterId) {
}
