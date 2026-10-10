package com.serenia.platform.alertsandsafety.domain.model.commands;

import java.util.UUID;

/**
 * Intention of the older adult to ask for immediate help.
 */
public record TriggerEmergencyAlertCommand(UUID olderAdultId) {
}
