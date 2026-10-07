package com.serenia.platform.wellbeingmonitoring.domain.model.commands;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Intention of evaluating the wellbeing from an answered check-in.
 */
public record EvaluateWellbeingPatternCommand(UUID olderAdultId, UUID checkInId, LocalDate checkDate, String mood, String positiveActivity) {
}
