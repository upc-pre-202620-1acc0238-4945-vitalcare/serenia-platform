package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(name = "AssignCareShiftRequest", description = "Request payload for taking the care shift of a date")
public record AssignCareShiftResource(
        @NotNull(message = "{validation.not-blank}")
        @Schema(description = "Date of the shift, in the older adult's time zone", example = "2026-10-10")
        LocalDate shiftDate) {
}
