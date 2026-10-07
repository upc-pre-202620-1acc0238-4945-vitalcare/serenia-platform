package com.serenia.platform.carecircle.domain.model.commands;

import java.util.UUID;

/**
 * Intention of a relative to join a care circle through an invitation code.
 *
 * @param code              the code shared by the older adult
 * @param relativeId        the authenticated relative
 * @param relationshipLabel the relationship declared by the relative, optional
 */
public record RedeemInvitationCodeCommand(String code, UUID relativeId, String relationshipLabel) {
}
