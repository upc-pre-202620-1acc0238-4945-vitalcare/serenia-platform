package com.serenia.platform.carecircle.domain.model.commands;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Intention of a relative to take the care shift of a date.
 */
public record AssignCareShiftCommand(UUID careCircleId, UUID relativeId, LocalDate shiftDate) {
}
