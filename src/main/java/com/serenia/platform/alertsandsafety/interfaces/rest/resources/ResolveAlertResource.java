package com.serenia.platform.alertsandsafety.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "ResolveAlertRequest", description = "Request payload for resolving an alert")
public record ResolveAlertResource(
        @Size(max = 300, message = "{validation.size.max}")
        @Schema(description = "How the alert was resolved", example = "Llamé a mamá, estaba bien", nullable = true)
        String resolutionNote) {
}
