package com.serenia.platform.iam.interfaces.rest;

import com.serenia.platform.iam.domain.model.commands.SignOutCommand;
import com.serenia.platform.iam.domain.services.SessionCommandService;
import com.serenia.platform.iam.infrastructure.authorization.sfs.model.AuthenticatedUserPrincipal;
import com.serenia.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.serenia.platform.iam.interfaces.rest.resources.SignInResource;
import com.serenia.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.serenia.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.serenia.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for signing in (opening a session) and signing out (closing it).
 */
@RestController
@RequestMapping(value = "/api/v1/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Sessions", description = "Sign-in and sign-out endpoints")
public class SessionsController {

    private final SessionCommandService sessionCommandService;

    public SessionsController(SessionCommandService sessionCommandService) {
        this.sessionCommandService = sessionCommandService;
    }

    @PostMapping
    @Operation(summary = "Sign in", description = "Authenticates a user and opens a session, returning the issued bearer token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Session opened successfully.",
                    content = @Content(schema = @Schema(implementation = AuthenticatedUserResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "401", description = "Invalid email or password."),
            @ApiResponse(responseCode = "422", description = "The account is not active.")})
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInResource resource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                sessionCommandService.handle(command),
                signIn -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        signIn.getLeft(), signIn.getMiddle(), signIn.getRight()),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Sign out", description = "Closes a session of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Session closed successfully."),
            @ApiResponse(responseCode = "401", description = "Missing or invalid session."),
            @ApiResponse(responseCode = "403", description = "The session belongs to another user."),
            @ApiResponse(responseCode = "404", description = "Session not found.")})
    public ResponseEntity<?> signOut(
            @PathVariable UUID sessionId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new SignOutCommand(sessionId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                sessionCommandService.handle(command),
                _ -> null,
                HttpStatus.NO_CONTENT);
    }
}
