package com.serenia.platform.carecircle.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "InvitationCodeResponse", description = "Invitation code and its validity")
public record InvitationCodeResource(
        @Schema(description = "Invitation code identifier", example = "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d")
        UUID id,

        @Schema(description = "Code to share with the relative", example = "K7M4QX2P")
        String code,

        @Schema(description = "Code status", example = "PENDING", allowableValues = {"PENDING", "USED", "EXPIRED"})
        String status,

        @Schema(description = "Instant the code stops being redeemable, in UTC", example = "2026-10-09T15:30:00Z")
        Instant expiresAt) {
}
