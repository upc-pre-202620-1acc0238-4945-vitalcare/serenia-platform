package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "EditSharedNoteRequest", description = "Request payload for editing a shared note")
public record EditSharedNoteResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "New content of the note", example = "La cita con el cardiólogo se movió al lunes.")
        String content) {
}
