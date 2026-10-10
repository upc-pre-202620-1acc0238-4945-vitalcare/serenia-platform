package com.serenia.platform.alertsandsafety.domain.model.commands;

import java.util.UUID;

/**
 * Intention of a relative to mark an inactivity alert as resolved.
 */
public record ResolveInactivityAlertCommand(UUID alertId, UUID relativeId, String resolutionNote) {
}
