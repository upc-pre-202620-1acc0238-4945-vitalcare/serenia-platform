package com.serenia.platform.carecircle.domain.model.queries;

import java.util.UUID;

/**
 * Query of the care circle of an older adult.
 */
public record GetCareCircleByOlderAdultIdQuery(UUID olderAdultId) {
}
