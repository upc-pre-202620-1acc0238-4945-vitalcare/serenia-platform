package com.serenia.platform.socialcompanionship.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "SocialReminderResponse", description = "Social-contact reminder of the older adult")
public record SocialReminderResource(
        @Schema(description = "Reminder identifier", example = "2c3d4e5f-6a7b-4c8d-9e0f-1a2b3c4d5e6f")
        UUID id,

        @Schema(description = "Older adult who scheduled the reminder", example = "3f2b8c9e-6a1d-4f0e-9b7a-2c5d8e1f4a6b")
        UUID olderAdultId,

        @Schema(description = "Title", example = "Llamar a Marta")
        String title,

        @Schema(description = "Description", example = "Preguntarle cómo le fue en su viaje", nullable = true)
        String description,

        @Schema(description = "Local date and time it is presented, in the older adult's time zone", example = "2026-10-08T17:30:00")
        LocalDateTime remindAt,

        @Schema(description = "Reminder status", example = "SCHEDULED",
                allowableValues = {"SCHEDULED", "PRESENTED", "POSTPONED", "COMPLETED", "CANCELED", "MISSED"})
        String status,

        @Schema(description = "Instant it was completed, in UTC", nullable = true)
        Instant completedAt) {
}
