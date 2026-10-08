package com.serenia.platform.alertsandsafety.domain.model.commands;

import java.util.UUID;

/**
 * Intention of a relative to mark an emergency as resolved.
 */
public record ResolveEmergencyAlertCommand(UUID alertId, UUID relativeId, String resolutionNote) {
}
