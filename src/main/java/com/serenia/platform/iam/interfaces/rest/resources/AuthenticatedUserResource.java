package com.serenia.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "AuthenticatedUserResponse", description = "Issued session token together with the authenticated user")
public record AuthenticatedUserResource(
        @Schema(description = "Identifier of the opened session", example = "9a7c1e2d-3b4f-4c5d-8e6f-7a8b9c0d1e2f")
        UUID sessionId,

        @Schema(description = "JWT bearer token for subsequent authenticated requests",
                example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Instant the token expires, in UTC", example = "2026-10-14T15:30:00Z")
        Instant expiresAt,

        @Schema(description = "Authenticated user")
        UserResource user) {
}
