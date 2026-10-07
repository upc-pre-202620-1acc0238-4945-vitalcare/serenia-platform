package com.serenia.platform.wellbeingmonitoring.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "WellbeingSuggestionResponse", description = "Action suggestion addressed to the relatives")
public record WellbeingSuggestionResource(
        @Schema(description = "Suggestion identifier", example = "2b3c4d5e-6f7a-4b8c-9d0e-1f2a3b4c5d6e")
        UUID id,

        @Schema(description = "Older adult the suggestion is about", example = "3f2b8c9e-6a1d-4f0e-9b7a-2c5d8e1f4a6b")
        UUID olderAdultId,

        @Schema(description = "Discomfort pattern that originated the suggestion", example = "4d5e6f7a-8b9c-4d0e-8f1a-2b3c4d5e6f7a")
        UUID patternId,

        @Schema(description = "Recommendation", example = "Lleva 3 días seguidos con el ánimo bajo. Puede ser un buen momento para llamarle o visitarle.")
        String message,

        @Schema(description = "Suggestion status", example = "ACTIVE", allowableValues = {"ACTIVE", "DISMISSED"})
        String status,

        @Schema(description = "Instant the suggestion was issued, in UTC", example = "2026-10-08T15:30:00Z")
        Instant issuedAt) {
}
