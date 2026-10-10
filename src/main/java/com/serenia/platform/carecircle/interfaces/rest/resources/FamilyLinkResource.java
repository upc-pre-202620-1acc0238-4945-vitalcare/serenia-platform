package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "FamilyLinkResponse", description = "Link of a relative with a care circle")
public record FamilyLinkResource(
        @Schema(description = "Family link identifier", example = "c3d4e5f6-a7b8-4c9d-8e0f-1a2b3c4d5e6f")
        UUID id,

        @Schema(description = "Care circle of the link", example = "6b1f0c2e-8d4a-4b7e-9f3c-1a2b3c4d5e6f")
        UUID careCircleId,

        @Schema(description = "Linked relative", example = "9d8c7b6a-5f4e-4d3c-8b2a-1f0e9d8c7b6a")
        UUID relativeId,

        @Schema(description = "Relationship with the older adult", example = "hija", nullable = true)
        String relationshipLabel,

        @Schema(description = "Link status", example = "ACTIVE", allowableValues = {"ACTIVE", "REVOKED"})
        String status,

        @Schema(description = "Instant the link became active, in UTC", example = "2026-10-07T16:00:00Z")
        Instant linkedAt) {
}
