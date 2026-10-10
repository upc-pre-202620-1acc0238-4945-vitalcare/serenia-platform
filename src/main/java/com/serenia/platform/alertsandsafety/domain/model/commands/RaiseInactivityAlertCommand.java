package com.serenia.platform.alertsandsafety.domain.model.commands;

import java.util.UUID;

/**
 * Intention of raising an inactivity alert.
 */
public record RaiseInactivityAlertCommand(UUID olderAdultId, UUID checkInId) {
}
