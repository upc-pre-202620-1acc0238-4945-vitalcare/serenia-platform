package com.serenia.platform.carecircle.domain.model.commands;

import java.util.UUID;

/**
 * Intention of marking as expired a code whose validity has ended.
 */
public record ExpireInvitationCodeCommand(UUID careCircleId, UUID invitationCodeId) {
}
