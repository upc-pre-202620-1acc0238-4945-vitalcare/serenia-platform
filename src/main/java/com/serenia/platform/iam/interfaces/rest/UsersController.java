package com.serenia.platform.iam.interfaces.rest;

import com.serenia.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.serenia.platform.iam.domain.model.valueobjects.UserRole;
import com.serenia.platform.iam.domain.services.UserCommandService;
import com.serenia.platform.iam.domain.services.UserQueryService;
import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.iam.interfaces.rest.resources.ChangePasswordResource;
import com.serenia.platform.iam.interfaces.rest.resources.RegisterUserResource;
import com.serenia.platform.iam.interfaces.rest.resources.UpdateProfileDataResource;
import com.serenia.platform.iam.interfaces.rest.resources.UpdateUserPhotoResource;
import com.serenia.platform.iam.interfaces.rest.resources.UserResource;
import com.serenia.platform.iam.interfaces.rest.transform.ChangePasswordCommandFromResourceAssembler;
import com.serenia.platform.iam.interfaces.rest.transform.RegisterDistantRelativeCommandFromResourceAssembler;
import com.serenia.platform.iam.interfaces.rest.transform.RegisterOlderAdultCommandFromResourceAssembler;
import com.serenia.platform.iam.interfaces.rest.transform.UpdateProfileDataCommandFromResourceAssembler;
import com.serenia.platform.iam.interfaces.rest.transform.UpdateUserPhotoCommandFromResourceAssembler;
import com.serenia.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for the registration, retrieval and profile management of users.
 *
 * <p>Registration is public and picks the command according to the requested role.
 * Modifications only proceed when {@code {userId}} matches the authenticated user;
 * otherwise the controller answers 403 Forbidden.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User registration and profile endpoints")
public class UsersController {

    private static final String NOT_ACCOUNT_OWNER = "user.not.account.owner";

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public UsersController(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    @PostMapping
    @Operation(summary = "Register user", description = "Registers an older adult or a distant relative according to the requested role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully.",
                    content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "409", description = "Email already registered.")})
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserResource resource) {
        var result = UserRole.valueOf(resource.role()) == UserRole.OLDER_ADULT
                ? userCommandService.handle(RegisterOlderAdultCommandFromResourceAssembler.toCommandFromResource(resource))
                : userCommandService.handle(RegisterDistantRelativeCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id", description = "Gets an account by its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid session."),
            @ApiResponse(responseCode = "404", description = "User not found.")})
    public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
        var user = userQueryService.handle(new GetUserByIdQuery(userId));
        if (user.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.notFound("user", userId.toString()));
        }
        return ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user.get()));
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "Update profile data", description = "Updates the personal data of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile data updated successfully.",
                    content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "403", description = "The user is not the account owner."),
            @ApiResponse(responseCode = "404", description = "User not found.")})
    public ResponseEntity<?> updateProfileData(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateProfileDataResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        if (!isAccountOwner(principal, userId)) return notAccountOwner();
        var command = UpdateProfileDataCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                userCommandService.handle(command),
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @PutMapping("/{userId}/photo")
    @Operation(summary = "Update user photo", description = "Replaces the profile photo of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo updated successfully.",
                    content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "403", description = "The user is not the account owner."),
            @ApiResponse(responseCode = "404", description = "User not found.")})
    public ResponseEntity<?> updateUserPhoto(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserPhotoResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        if (!isAccountOwner(principal, userId)) return notAccountOwner();
        var command = UpdateUserPhotoCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                userCommandService.handle(command),
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "Change password",
            description = "Changes the password of the authenticated user after verifying the current one. "
                    + "Every other active session of the user is revoked.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password changed successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "403", description = "The user is not the account owner."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "422", description = "Current password is incorrect.")})
    public ResponseEntity<?> changePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangePasswordResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        if (!isAccountOwner(principal, userId)) return notAccountOwner();
        var command = ChangePasswordCommandFromResourceAssembler.toCommandFromResource(
                userId, principal.sessionId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                userCommandService.handle(command),
                _ -> null,
                HttpStatus.NO_CONTENT);
    }

    private static boolean isAccountOwner(AuthenticatedUserPrincipal principal, UUID userId) {
        return principal != null && principal.userId().equals(userId);
    }

    private static ResponseEntity<?> notAccountOwner() {
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                ApplicationError.forbidden(NOT_ACCOUNT_OWNER));
    }
}
