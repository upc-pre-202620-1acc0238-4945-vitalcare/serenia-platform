package com.serenia.platform.dailycheckin.interfaces.rest;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences;
import com.serenia.platform.dailycheckin.domain.model.commands.ActivateDailyPauseCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.ResumeDailyCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInPreferencesByOlderAdultIdQuery;
import com.serenia.platform.dailycheckin.domain.services.CheckInPreferencesCommandService;
import com.serenia.platform.dailycheckin.domain.services.CheckInPreferencesQueryService;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.CheckInPreferencesResource;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.ScheduleCheckInResource;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.UpdateSimplifiedModeResource;
import com.serenia.platform.dailycheckin.interfaces.rest.transform.CheckInPreferencesResourceFromEntityAssembler;
import com.serenia.platform.dailycheckin.interfaces.rest.transform.ScheduleCheckInCommandFromResourceAssembler;
import com.serenia.platform.dailycheckin.interfaces.rest.transform.SimplifiedModeCommandFromResourceAssembler;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * REST controller for querying and changing the check-in preferences.
 *
 * <p>Only the older adult can read or change their own preferences. Every change answers with
 * the updated preferences.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/check-in-preferences", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Check-in Preferences", description = "Daily check-in preference endpoints")
public class CheckInPreferencesController {

    private static final String NOT_OWN_PREFERENCES = "check.in.preferences.not.owner";

    private final CheckInPreferencesCommandService checkInPreferencesCommandService;
    private final CheckInPreferencesQueryService checkInPreferencesQueryService;

    public CheckInPreferencesController(CheckInPreferencesCommandService checkInPreferencesCommandService,
                                        CheckInPreferencesQueryService checkInPreferencesQueryService) {
        this.checkInPreferencesCommandService = checkInPreferencesCommandService;
        this.checkInPreferencesQueryService = checkInPreferencesQueryService;
    }

    @GetMapping(params = "olderAdultId")
    @Operation(summary = "Get check-in preferences", description = "Gets the check-in preferences of the authenticated older adult.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferences retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = CheckInPreferencesResource.class))),
            @ApiResponse(responseCode = "403", description = "Only the older adult can read their preferences."),
            @ApiResponse(responseCode = "404", description = "Preferences not found.")})
    public ResponseEntity<?> getCheckInPreferences(
            @RequestParam UUID olderAdultId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                checkInPreferencesQueryService.handle(new GetCheckInPreferencesByOlderAdultIdQuery(olderAdultId, principal.userId())),
                preferences -> CheckInPreferencesResourceFromEntityAssembler.toResourceFromEntity(
                        preferences.getLeft(), preferences.getRight()),
                HttpStatus.OK);
    }

    @PutMapping("/{preferencesId}/reminder-time")
    @Operation(summary = "Change check-in time", description = "Changes the local time of the check-in; it applies from the next check-in.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check-in time changed successfully.",
                    content = @Content(schema = @Schema(implementation = CheckInPreferencesResource.class))),
            @ApiResponse(responseCode = "400", description = "The time is outside the 06:00 to 20:00 range."),
            @ApiResponse(responseCode = "403", description = "Only the older adult can change their preferences.")})
    public ResponseEntity<?> scheduleCheckIn(
            @PathVariable UUID preferencesId,
            @Valid @RequestBody ScheduleCheckInResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = ScheduleCheckInCommandFromResourceAssembler.toCommandFromResource(principal.userId(), resource);
        return changeOwnPreferences(preferencesId, principal, () -> checkInPreferencesCommandService.handle(command));
    }

    @PostMapping("/{preferencesId}/daily-pause")
    @Operation(summary = "Pause today's questions", description = "Pauses the questions of the older adult's current day.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions paused successfully.",
                    content = @Content(schema = @Schema(implementation = CheckInPreferencesResource.class))),
            @ApiResponse(responseCode = "403", description = "Only the older adult can pause their questions."),
            @ApiResponse(responseCode = "409", description = "The questions were already paused.")})
    public ResponseEntity<?> activateDailyPause(
            @PathVariable UUID preferencesId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new ActivateDailyPauseCommand(principal.userId());
        return changeOwnPreferences(preferencesId, principal, () -> checkInPreferencesCommandService.handle(command));
    }

    @DeleteMapping("/{preferencesId}/daily-pause")
    @Operation(summary = "Resume today's questions",
            description = "Resumes the questions; if the check-in deadline already passed, it gets a new one of the same length.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions resumed successfully.",
                    content = @Content(schema = @Schema(implementation = CheckInPreferencesResource.class))),
            @ApiResponse(responseCode = "403", description = "Only the older adult can resume their questions."),
            @ApiResponse(responseCode = "409", description = "The questions were not paused.")})
    public ResponseEntity<?> resumeDailyCheckIn(
            @PathVariable UUID preferencesId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new ResumeDailyCheckInCommand(principal.userId());
        return changeOwnPreferences(preferencesId, principal, () -> checkInPreferencesCommandService.handle(command));
    }

    @PutMapping("/{preferencesId}/simplified-mode")
    @Operation(summary = "Enable or disable simplified mode", description = "Shows or hides the simplified interface.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Simplified mode updated successfully.",
                    content = @Content(schema = @Schema(implementation = CheckInPreferencesResource.class))),
            @ApiResponse(responseCode = "403", description = "Only the older adult can change their preferences.")})
    public ResponseEntity<?> updateSimplifiedMode(
            @PathVariable UUID preferencesId,
            @Valid @RequestBody UpdateSimplifiedModeResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = SimplifiedModeCommandFromResourceAssembler.toCommandFromResource(principal.userId(), resource);
        return changeOwnPreferences(preferencesId, principal, () -> switch (command) {
            case SimplifiedModeCommandFromResourceAssembler.Enable enable -> checkInPreferencesCommandService.handle(enable.command());
            case SimplifiedModeCommandFromResourceAssembler.Disable disable -> checkInPreferencesCommandService.handle(disable.command());
        });
    }

    /**
     * Runs the change only if {@code preferencesId} belongs to the authenticated older adult,
     * and answers with the preferences as they are after the change.
     */
    private ResponseEntity<?> changeOwnPreferences(UUID preferencesId, AuthenticatedUserPrincipal principal,
                                                   Supplier<Result<CheckInPreferences, ApplicationError>> change) {
        var ownPreferencesQuery = new GetCheckInPreferencesByOlderAdultIdQuery(principal.userId(), principal.userId());
        var isOwnPreferences = checkInPreferencesQueryService.handle(ownPreferencesQuery).toOptional()
                .map(own -> own.getLeft().getId().value().equals(preferencesId))
                .orElse(false);
        if (!isOwnPreferences)
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.forbidden(NOT_OWN_PREFERENCES));

        var result = change.get().flatMap(_ -> checkInPreferencesQueryService.handle(ownPreferencesQuery));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                preferences -> CheckInPreferencesResourceFromEntityAssembler.toResourceFromEntity(
                        preferences.getLeft(), preferences.getRight()),
                HttpStatus.OK);
    }
}
