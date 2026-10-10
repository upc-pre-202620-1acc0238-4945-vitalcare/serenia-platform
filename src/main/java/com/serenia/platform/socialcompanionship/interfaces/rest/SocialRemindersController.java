package com.serenia.platform.socialcompanionship.interfaces.rest;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.serenia.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.acl.ExternalIamService;
import com.serenia.platform.socialcompanionship.domain.model.commands.CancelSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.CompleteSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.PostponeSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetActiveSocialRemindersQuery;
import com.serenia.platform.socialcompanionship.domain.services.SocialReminderCommandService;
import com.serenia.platform.socialcompanionship.domain.services.SocialReminderQueryService;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.ScheduleSocialReminderResource;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.SocialReminderResource;
import com.serenia.platform.socialcompanionship.interfaces.rest.transform.ScheduleSocialReminderCommandFromResourceAssembler;
import com.serenia.platform.socialcompanionship.interfaces.rest.transform.SocialReminderResourceFromEntityAssembler;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for the scheduling and management of social reminders by the older adult
 * themselves. Times are received and returned in the older adult's local time.
 */
@RestController
@RequestMapping(value = "/api/v1/social-reminders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Social Reminders", description = "Social-contact reminders of the older adult")
public class SocialRemindersController {

    private static final String OLDER_ADULT_ROLE = "OLDER_ADULT";
    private static final String ONLY_OLDER_ADULTS = "social.reminder.only.older.adults";

    private final SocialReminderCommandService socialReminderCommandService;
    private final SocialReminderQueryService socialReminderQueryService;
    private final ExternalIamService externalIamService;

    public SocialRemindersController(SocialReminderCommandService socialReminderCommandService,
                                     SocialReminderQueryService socialReminderQueryService,
                                     ExternalIamService externalIamService) {
        this.socialReminderCommandService = socialReminderCommandService;
        this.socialReminderQueryService = socialReminderQueryService;
        this.externalIamService = externalIamService;
    }

    @PostMapping
    @Operation(summary = "Schedule reminder", description = "Schedules a reminder at a local date and time of the older adult.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reminder scheduled.",
                    content = @Content(schema = @Schema(implementation = SocialReminderResource.class))),
            @ApiResponse(responseCode = "400", description = "Missing title, or the date and time already passed."),
            @ApiResponse(responseCode = "403", description = "Only older adults can schedule reminders.")})
    public ResponseEntity<?> scheduleSocialReminder(
            @Valid @RequestBody ScheduleSocialReminderResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        if (!OLDER_ADULT_ROLE.equals(principal.role()))
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.forbidden(ONLY_OLDER_ADULTS));
        var command = ScheduleSocialReminderCommandFromResourceAssembler.toCommandFromResource(principal.userId(), resource);
        var timeZone = externalIamService.fetchTimeZone(principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                socialReminderCommandService.handle(command),
                reminder -> SocialReminderResourceFromEntityAssembler.toResourceFromEntity(reminder, timeZone),
                HttpStatus.CREATED);
    }

    @GetMapping(params = "olderAdultId")
    @Operation(summary = "Get active reminders", description = "Gets the scheduled, presented or postponed reminders, ordered by time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminders retrieved."),
            @ApiResponse(responseCode = "403", description = "Only the older adult can see their reminders.")})
    public ResponseEntity<?> getActiveSocialReminders(
            @RequestParam UUID olderAdultId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetActiveSocialRemindersQuery(olderAdultId, principal.userId());
        var timeZone = externalIamService.fetchTimeZone(olderAdultId);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                socialReminderQueryService.handle(query),
                reminders -> reminders.stream()
                        .map(reminder -> SocialReminderResourceFromEntityAssembler.toResourceFromEntity(reminder, timeZone))
                        .toList(),
                HttpStatus.OK);
    }

    @PostMapping("/{socialReminderId}/postpone")
    @Operation(summary = "Postpone reminder", description = "Postpones a presented reminder 60 minutes, within the same local day.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder postponed.",
                    content = @Content(schema = @Schema(implementation = SocialReminderResource.class))),
            @ApiResponse(responseCode = "403", description = "The reminder belongs to another older adult."),
            @ApiResponse(responseCode = "404", description = "Reminder not found."),
            @ApiResponse(responseCode = "409", description = "The reminder is not presented, or there is no time left today.")})
    public ResponseEntity<?> postponeSocialReminder(
            @PathVariable UUID socialReminderId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new PostponeSocialReminderCommand(socialReminderId, principal.userId());
        var timeZone = externalIamService.fetchTimeZone(principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                socialReminderCommandService.handle(command),
                reminder -> SocialReminderResourceFromEntityAssembler.toResourceFromEntity(reminder, timeZone),
                HttpStatus.OK);
    }

    @PostMapping("/{socialReminderId}/complete")
    @Operation(summary = "Complete reminder", description = "Marks the reminder as done so it is not presented again.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder completed.",
                    content = @Content(schema = @Schema(implementation = SocialReminderResource.class))),
            @ApiResponse(responseCode = "403", description = "The reminder belongs to another older adult."),
            @ApiResponse(responseCode = "404", description = "Reminder not found."),
            @ApiResponse(responseCode = "409", description = "The reminder was already closed.")})
    public ResponseEntity<?> completeSocialReminder(
            @PathVariable UUID socialReminderId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new CompleteSocialReminderCommand(socialReminderId, principal.userId());
        var timeZone = externalIamService.fetchTimeZone(principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                socialReminderCommandService.handle(command),
                reminder -> SocialReminderResourceFromEntityAssembler.toResourceFromEntity(reminder, timeZone),
                HttpStatus.OK);
    }

    @PostMapping("/{socialReminderId}/cancel")
    @Operation(summary = "Cancel reminder", description = "Cancels a reminder that is no longer needed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder canceled.",
                    content = @Content(schema = @Schema(implementation = SocialReminderResource.class))),
            @ApiResponse(responseCode = "403", description = "The reminder belongs to another older adult."),
            @ApiResponse(responseCode = "404", description = "Reminder not found."),
            @ApiResponse(responseCode = "409", description = "The reminder was already closed.")})
    public ResponseEntity<?> cancelSocialReminder(
            @PathVariable UUID socialReminderId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new CancelSocialReminderCommand(socialReminderId, principal.userId());
        var timeZone = externalIamService.fetchTimeZone(principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                socialReminderCommandService.handle(command),
                reminder -> SocialReminderResourceFromEntityAssembler.toResourceFromEntity(reminder, timeZone),
                HttpStatus.OK);
    }
}
