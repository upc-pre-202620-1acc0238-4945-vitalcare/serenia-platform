package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RedeemInvitationCodeRequest", description = "Request payload for joining a care circle with a code")
public record RedeemInvitationCodeResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Code shared by the older adult", example = "K7M4QX2P")
        String code,

        @Size(max = 60, message = "{validation.size.max}")
        @Schema(description = "Relationship with the older adult", example = "hija", nullable = true)
        String relationshipLabel) {
}
