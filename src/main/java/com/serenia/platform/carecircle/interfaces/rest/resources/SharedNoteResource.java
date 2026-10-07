package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "SharedNoteResponse", description = "Note shared with every relative of a care circle")
public record SharedNoteResource(
        @Schema(description = "Shared note identifier", example = "f6a7b8c9-d0e1-4f2a-9b3c-4d5e6f7a8b9c")
        UUID id,

        @Schema(description = "Care circle of the note", example = "6b1f0c2e-8d4a-4b7e-9f3c-1a2b3c4d5e6f")
        UUID careCircleId,

        @Schema(description = "Relative who wrote the note", example = "9d8c7b6a-5f4e-4d3c-8b2a-1f0e9d8c7b6a")
        UUID authorId,

        @Schema(description = "Content of the note", example = "Mamá tiene cita con el cardiólogo el viernes a las 10:00.")
        String content,

        @Schema(description = "Instant the note was created, in UTC", example = "2026-10-07T16:00:00Z")
        Instant createdAt,

        @Schema(description = "Instant of the last edition, in UTC", example = "2026-10-07T16:30:00Z")
        Instant updatedAt) {
}
