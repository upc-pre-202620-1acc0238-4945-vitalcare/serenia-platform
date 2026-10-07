package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "CareCircleResponse", description = "Care circle of an older adult")
public record CareCircleResource(
        @Schema(description = "Care circle identifier", example = "6b1f0c2e-8d4a-4b7e-9f3c-1a2b3c4d5e6f")
        UUID id,

        @Schema(description = "Older adult who owns the circle", example = "3f2b8c9e-6a1d-4f0e-9b7a-2c5d8e1f4a6b")
        UUID olderAdultId,

        @Schema(description = "Instant the circle was created, in UTC", example = "2026-10-07T15:30:00Z")
        Instant createdAt) {
}
