package com.serenia.platform.carecircle.domain.model.commands;

import java.util.UUID;

/**
 * Intention of registering the link of a relative after the redemption of their code.
 */
public record EstablishFamilyLinkCommand(UUID careCircleId, UUID relativeId, String relationshipLabel) {
}
