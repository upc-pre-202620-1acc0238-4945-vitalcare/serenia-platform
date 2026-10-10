package com.serenia.platform.alertsandsafety.domain.model.commands;

import java.util.UUID;

/**
 * Intention of confirming that the emergency became available to the relatives.
 */
public record ConfirmEmergencyAlertDeliveryCommand(UUID alertId) {
}
