package com.serenia.platform.carecircle.domain.model.queries;

import java.util.UUID;

/**
 * Query of the care circles a relative is actively linked to.
 */
public record GetActiveFamilyLinksByRelativeIdQuery(UUID relativeId) {
}
