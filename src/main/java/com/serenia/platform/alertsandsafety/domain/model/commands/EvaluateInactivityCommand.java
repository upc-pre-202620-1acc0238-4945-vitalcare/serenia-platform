package com.serenia.platform.alertsandsafety.domain.model.commands;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Intention of evaluating whether a missed check-in must raise an alert.
 */
public record EvaluateInactivityCommand(UUID olderAdultId, UUID checkInId, LocalDate checkDate) {
}
