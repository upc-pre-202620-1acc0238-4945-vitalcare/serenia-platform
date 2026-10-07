package com.serenia.platform.wellbeingmonitoring.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "SmallWinResponse", description = "Good day or positive activity of the older adult")
public record SmallWinResource(
        @Schema(description = "Small win identifier", example = "5e6f7a8b-9c0d-4e1f-8a2b-3c4d5e6f7a8b")
        UUID id,

        @Schema(description = "Older adult of the small win", example = "3f2b8c9e-6a1d-4f0e-9b7a-2c5d8e1f4a6b")
        UUID olderAdultId,

        @Schema(description = "Check-in it comes from", example = "1c2d3e4f-5a6b-4c7d-8e9f-0a1b2c3d4e5f")
        UUID checkInId,

        @Schema(description = "Positive activity or description of the good day", example = "Regué mis plantas")
        String description,

        @Schema(description = "Instant it was recorded, in UTC", example = "2026-10-08T15:20:00Z")
        Instant recordedAt) {
}
