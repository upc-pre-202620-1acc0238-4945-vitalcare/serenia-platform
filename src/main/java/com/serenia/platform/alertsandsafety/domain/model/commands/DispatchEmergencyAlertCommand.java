package com.serenia.platform.alertsandsafety.domain.model.commands;

import java.util.UUID;

/**
 * Intention of notifying an emergency to the linked relatives.
 */
public record DispatchEmergencyAlertCommand(UUID alertId) {
}
