package com.serenia.platform.alertsandsafety.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "AlertResponse", description = "Alert of either type, with its attention status")
public record AlertResource(
        @Schema(description = "Alert identifier", example = "4e5f6a7b-8c9d-4e0f-9a1b-2c3d4e5f6a7b")
        UUID id,

        @Schema(description = "Older adult of the alert", example = "3f2b8c9e-6a1d-4f0e-9b7a-2c5d8e1f4a6b")
        UUID olderAdultId,

        @Schema(description = "Alert type", example = "EMERGENCY", allowableValues = {"EMERGENCY", "INACTIVITY"})
        String type,

        @Schema(description = "Alert status", example = "DISPATCHED",
                allowableValues = {"RAISED", "DISPATCHED", "DISPATCH_FAILED", "ACKNOWLEDGED", "RESOLVED"})
        String status,

        @Schema(description = "Missed check-in that originated an inactivity alert", nullable = true)
        UUID checkInId,

        @Schema(description = "Instant the alert was raised, in UTC", example = "2026-10-07T15:00:00Z")
        Instant triggeredAt,

        @Schema(description = "Whether the emergency reached the relatives; always false for inactivity alerts", example = "true")
        boolean deliveryConfirmed,

        @Schema(description = "Relative attending the alert", nullable = true)
        UUID acknowledgedBy,

        @Schema(description = "Instant the alert was acknowledged, in UTC", nullable = true)
        Instant acknowledgedAt,

        @Schema(description = "Relative who resolved the alert", nullable = true)
        UUID resolvedBy,

        @Schema(description = "Instant the alert was resolved, in UTC", nullable = true)
        Instant resolvedAt,

        @Schema(description = "How the alert was resolved", example = "Llamé a mamá, estaba bien", nullable = true)
        String resolutionNote) {
}
