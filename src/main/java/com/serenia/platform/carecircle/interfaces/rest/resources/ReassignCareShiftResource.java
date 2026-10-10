package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "ReassignCareShiftRequest", description = "Request payload for changing the relative in charge of a shift")
public record ReassignCareShiftResource(
        @NotNull(message = "{validation.not-blank}")
        @Schema(description = "Relative who takes the shift", example = "9d8c7b6a-5f4e-4d3c-8b2a-1f0e9d8c7b6a")
        UUID relativeId) {
}
