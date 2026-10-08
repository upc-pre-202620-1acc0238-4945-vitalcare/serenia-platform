package com.serenia.platform.alertsandsafety.domain.model.commands;

import java.util.UUID;

/**
 * Intention of a relative to say they are attending an inactivity alert.
 */
public record AcknowledgeInactivityAlertCommand(UUID alertId, UUID relativeId) {
}
