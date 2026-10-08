package com.serenia.platform.alertsandsafety.domain.model.queries;

import java.util.UUID;

/**
 * Query of the alert history of an older adult, of both types, from the most recent to the oldest.
 */
public record GetAlertsByOlderAdultIdQuery(UUID olderAdultId, UUID requesterId) {
}
