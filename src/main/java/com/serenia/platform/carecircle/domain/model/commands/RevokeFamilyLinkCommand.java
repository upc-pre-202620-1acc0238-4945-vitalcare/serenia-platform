package com.serenia.platform.carecircle.domain.model.commands;

import java.util.UUID;

/**
 * Intention of withdrawing the access of a relative to a care circle.
 */
public record RevokeFamilyLinkCommand(UUID careCircleId, UUID familyLinkId, UUID requesterId) {
}
