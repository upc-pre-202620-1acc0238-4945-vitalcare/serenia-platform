package com.serenia.platform.alertsandsafety.interfaces.rest;

import com.serenia.platform.alertsandsafety.domain.model.commands.AcknowledgeEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.TriggerEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.services.EmergencyAlertCommandService;
import com.serenia.platform.alertsandsafety.interfaces.rest.resources.AlertResource;
import com.serenia.platform.alertsandsafety.interfaces.rest.resources.ResolveAlertResource;
import com.serenia.platform.alertsandsafety.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import com.serenia.platform.alertsandsafety.interfaces.rest.transform.ResolveEmergencyAlertCommandFromResourceAssembler;
import com.serenia.platform.shared.application.result.ApplicationError;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for the help button, reserved to the older adult, and the attention of
 * emergencies by the relatives.
 */
@RestController
@RequestMapping(value = "/api/v1/emergency-alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Emergency Alerts", description = "Help button and attention of emergencies")
public class EmergencyAlertsController {

    private static final String OLDER_ADULT_ROLE = "OLDER_ADULT";
    private static final String ONLY_OLDER_ADULTS = "alert.emergency.only.older.adults";

    private final EmergencyAlertCommandService emergencyAlertCommandService;

    public EmergencyAlertsController(EmergencyAlertCommandService emergencyAlertCommandService) {
        this.emergencyAlertCommandService = emergencyAlertCommandService;
    }

    @PostMapping
    @Operation(summary = "Press the help button",
            description = "Records the emergency and notifies the linked relatives. The answer tells whether the family "
                    + "was notified (DISPATCHED with deliveryConfirmed) or the dispatch failed (DISPATCH_FAILED).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Emergency recorded, in its final state.",
                    content = @Content(schema = @Schema(implementation = AlertResource.class))),
            @ApiResponse(responseCode = "403", description = "Only the older adult can press the help button.")})
    public ResponseEntity<?> triggerEmergencyAlert(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        if (!OLDER_ADULT_ROLE.equals(principal.role()))
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.forbidden(ONLY_OLDER_ADULTS));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                emergencyAlertCommandService.handle(new TriggerEmergencyAlertCommand(principal.userId())),
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @PostMapping("/{alertId}/acknowledge")
    @Operation(summary = "Acknowledge emergency", description = "Tells the family that the relative is attending the emergency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emergency acknowledged.",
                    content = @Content(schema = @Schema(implementation = AlertResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is not an actively linked relative."),
            @ApiResponse(responseCode = "404", description = "Alert not found."),
            @ApiResponse(responseCode = "409", description = "The emergency was already acknowledged or was not dispatched.")})
    public ResponseEntity<?> acknowledgeEmergencyAlert(
            @PathVariable UUID alertId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                emergencyAlertCommandService.handle(new AcknowledgeEmergencyAlertCommand(alertId, principal.userId())),
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @PostMapping("/{alertId}/resolve")
    @Operation(summary = "Resolve emergency", description = "Marks an acknowledged emergency as resolved, with an optional note.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emergency resolved.",
                    content = @Content(schema = @Schema(implementation = AlertResource.class))),
            @ApiResponse(responseCode = "400", description = "The note exceeds 300 characters."),
            @ApiResponse(responseCode = "403", description = "The user is not an actively linked relative."),
            @ApiResponse(responseCode = "404", description = "Alert not found."),
            @ApiResponse(responseCode = "409", description = "The emergency was not acknowledged or is already resolved.")})
    public ResponseEntity<?> resolveEmergencyAlert(
            @PathVariable UUID alertId,
            @Valid @RequestBody(required = false) ResolveAlertResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = ResolveEmergencyAlertCommandFromResourceAssembler.toCommandFromResource(alertId, principal.userId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                emergencyAlertCommandService.handle(command),
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
