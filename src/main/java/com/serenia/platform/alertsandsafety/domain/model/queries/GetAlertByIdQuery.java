package com.serenia.platform.alertsandsafety.domain.model.queries;

import java.util.UUID;

/**
 * Query of the detail of an alert.
 */
public record GetAlertByIdQuery(UUID alertId, UUID requesterId) {
}
