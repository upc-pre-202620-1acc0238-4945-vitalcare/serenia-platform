package com.serenia.platform.dailycheckin.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "CheckInResponse", description = "Daily check-in with the text of its question and whether the questions are paused")
public record CheckInResource(
        @Schema(description = "Check-in identifier", example = "1c2d3e4f-5a6b-4c7d-8e9f-0a1b2c3d4e5f")
        UUID id,

        @Schema(description = "Older adult of the check-in", example = "3f2b8c9e-6a1d-4f0e-9b7a-2c5d8e1f4a6b")
        UUID olderAdultId,

        @Schema(description = "Date of the check-in, in the older adult's time zone", example = "2026-10-08")
        LocalDate checkDate,

        @Schema(description = "Question presented that day", example = "¿Cómo se siente hoy?")
        String questionText,

        @Schema(description = "Instant the question is presented, in UTC", example = "2026-10-08T15:00:00Z")
        Instant scheduledFor,

        @Schema(description = "Instant the check-in can no longer be answered, in UTC", example = "2026-10-08T18:00:00Z")
        Instant deadlineAt,

        @Schema(description = "Check-in status", example = "PENDING",
                allowableValues = {"PENDING", "ANSWERED", "MISSED", "SKIPPED"})
        String status,

        @Schema(description = "Whether the older adult chose not to receive questions that day", example = "false")
        boolean paused,

        @Schema(description = "Reported mood", example = "GOOD", nullable = true,
                allowableValues = {"VERY_LOW", "LOW", "NEUTRAL", "GOOD", "VERY_GOOD"})
        String mood,

        @Schema(description = "Reported positive activity", example = "Caminé en el parque", nullable = true)
        String positiveActivity,

        @Schema(description = "Instant of the answer, in UTC", example = "2026-10-08T15:20:00Z", nullable = true)
        Instant answeredAt) {
}
