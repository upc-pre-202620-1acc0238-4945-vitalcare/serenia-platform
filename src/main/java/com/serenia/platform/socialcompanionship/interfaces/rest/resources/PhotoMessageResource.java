package com.serenia.platform.socialcompanionship.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "PhotoMessageResponse", description = "Photo a relative shares with the older adult")
public record PhotoMessageResource(
        @Schema(description = "Photo message identifier", example = "1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e")
        UUID id,

        @Schema(description = "Care circle the photo is shared in", example = "6b1f0c2e-8d4a-4b7e-9f3c-1a2b3c4d5e6f")
        UUID careCircleId,

        @Schema(description = "Relative who shared the photo", example = "9d8c7b6a-5f4e-4d3c-8b2a-1f0e9d8c7b6a")
        UUID senderId,

        @Schema(description = "Message status", example = "SHARED", allowableValues = {"DRAFT", "SHARED"})
        String status,

        @Schema(description = "Protected endpoint that serves the image file",
                example = "/api/v1/care-circles/6b1f0c2e-8d4a-4b7e-9f3c-1a2b3c4d5e6f/photo-messages/1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e/media")
        String mediaUrl,

        @Schema(description = "Instant of the selection, in UTC", example = "2026-10-07T15:00:00Z")
        Instant createdAt,

        @Schema(description = "Instant it was shared, in UTC; null while it is a draft", nullable = true)
        Instant sentAt,

        @Schema(description = "Whether the older adult already saw the photo", example = "false")
        boolean viewed) {
}
