package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "CareShiftResponse", description = "Care shift of a relative on a date")
public record CareShiftResource(
        @Schema(description = "Care shift identifier", example = "e5f6a7b8-c9d0-4e1f-8a2b-3c4d5e6f7a8b")
        UUID id,

        @Schema(description = "Care circle of the shift", example = "6b1f0c2e-8d4a-4b7e-9f3c-1a2b3c4d5e6f")
        UUID careCircleId,

        @Schema(description = "Relative in charge", example = "9d8c7b6a-5f4e-4d3c-8b2a-1f0e9d8c7b6a")
        UUID relativeId,

        @Schema(description = "Date of the shift, in the older adult's time zone", example = "2026-10-10")
        LocalDate shiftDate) {
}
