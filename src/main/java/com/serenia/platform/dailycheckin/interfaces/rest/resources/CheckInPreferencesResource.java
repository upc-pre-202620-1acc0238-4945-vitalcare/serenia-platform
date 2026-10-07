package com.serenia.platform.dailycheckin.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.UUID;

@Schema(name = "CheckInPreferencesResponse", description = "Daily check-in preferences of an older adult")
public record CheckInPreferencesResource(
        @Schema(description = "Preferences identifier", example = "7a8b9c0d-1e2f-4a3b-8c4d-5e6f7a8b9c0d")
        UUID id,

        @Schema(description = "Older adult the preferences belong to", example = "3f2b8c9e-6a1d-4f0e-9b7a-2c5d8e1f4a6b")
        UUID olderAdultId,

        @Schema(description = "Local time of the daily question", type = "string", example = "10:00:00")
        LocalTime reminderTime,

        @Schema(description = "Minutes to answer from the reminder time", example = "180")
        int timeLimitMinutes,

        @Schema(description = "Whether the interface is shown in simplified mode", example = "false")
        boolean simplifiedMode,

        @Schema(description = "Whether the questions are paused today", example = "false")
        boolean pausedToday) {
}
