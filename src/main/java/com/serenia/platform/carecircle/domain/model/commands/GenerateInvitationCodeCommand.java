package com.serenia.platform.carecircle.domain.model.commands;

import java.util.UUID;

/**
 * Intention of the older adult to generate a code to invite a relative.
 */
public record GenerateInvitationCodeCommand(UUID careCircleId, UUID requesterId) {
}
