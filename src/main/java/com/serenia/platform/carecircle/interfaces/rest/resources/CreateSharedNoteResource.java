package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "CreateSharedNoteRequest", description = "Request payload for writing a shared note")
public record CreateSharedNoteResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Content of the note", example = "Mamá tiene cita con el cardiólogo el viernes a las 10:00.")
        String content) {
}
