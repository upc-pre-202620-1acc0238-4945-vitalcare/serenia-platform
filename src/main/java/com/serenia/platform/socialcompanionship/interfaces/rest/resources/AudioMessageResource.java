package com.serenia.platform.socialcompanionship.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "AudioMessageResponse", description = "Voice message of the older adult for the relatives")
public record AudioMessageResource(
        @Schema(description = "Audio message identifier", example = "0a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d")
        UUID id,

        @Schema(description = "Care circle the audio is shared in", example = "6b1f0c2e-8d4a-4b7e-9f3c-1a2b3c4d5e6f")
        UUID careCircleId,

        @Schema(description = "Older adult who recorded the audio", example = "3f2b8c9e-6a1d-4f0e-9b7a-2c5d8e1f4a6b")
        UUID senderId,

        @Schema(description = "Message status", example = "SHARED", allowableValues = {"DRAFT", "SHARED"})
        String status,

        @Schema(description = "Duration in seconds", example = "42")
        int durationSeconds,

        @Schema(description = "Protected endpoint that serves the audio file",
                example = "/api/v1/care-circles/6b1f0c2e-8d4a-4b7e-9f3c-1a2b3c4d5e6f/audio-messages/0a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d/media")
        String mediaUrl,

        @Schema(description = "Instant of the recording, in UTC", example = "2026-10-07T15:00:00Z")
        Instant createdAt,

        @Schema(description = "Instant it was shared, in UTC; null while it is a draft", nullable = true)
        Instant sentAt,

        @Schema(description = "Whether the requester already played the audio", example = "false")
        boolean played) {
}
