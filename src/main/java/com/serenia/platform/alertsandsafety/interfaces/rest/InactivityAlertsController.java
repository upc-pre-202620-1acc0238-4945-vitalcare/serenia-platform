package com.serenia.platform.alertsandsafety.interfaces.rest;

import com.serenia.platform.alertsandsafety.domain.model.commands.AcknowledgeInactivityAlertCommand;
import com.serenia.platform.alertsandsafety.domain.services.InactivityAlertCommandService;
import com.serenia.platform.alertsandsafety.interfaces.rest.resources.AlertResource;
import com.serenia.platform.alertsandsafety.interfaces.rest.resources.ResolveAlertResource;
import com.serenia.platform.alertsandsafety.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import com.serenia.platform.alertsandsafety.interfaces.rest.transform.ResolveInactivityAlertCommandFromResourceAssembler;
import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
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
 * REST controller for the attention of inactivity alerts by the relatives.
 */
@RestController
@RequestMapping(value = "/api/v1/inactivity-alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Inactivity Alerts", description = "Attention of inactivity alerts")
public class InactivityAlertsController {

    private final InactivityAlertCommandService inactivityAlertCommandService;

    public InactivityAlertsController(InactivityAlertCommandService inactivityAlertCommandService) {
        this.inactivityAlertCommandService = inactivityAlertCommandService;
    }

    @PostMapping("/{alertId}/acknowledge")
    @Operation(summary = "Acknowledge inactivity alert", description = "Tells the family that the relative is attending the alert.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert acknowledged.",
                    content = @Content(schema = @Schema(implementation = AlertResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is not an actively linked relative."),
            @ApiResponse(responseCode = "404", description = "Alert not found."),
            @ApiResponse(responseCode = "409", description = "The alert was already acknowledged.")})
    public ResponseEntity<?> acknowledgeInactivityAlert(
            @PathVariable UUID alertId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                inactivityAlertCommandService.handle(new AcknowledgeInactivityAlertCommand(alertId, principal.userId())),
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @PostMapping("/{alertId}/resolve")
    @Operation(summary = "Resolve inactivity alert", description = "Marks an acknowledged alert as resolved, with an optional note.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert resolved.",
                    content = @Content(schema = @Schema(implementation = AlertResource.class))),
            @ApiResponse(responseCode = "400", description = "The note exceeds 300 characters."),
            @ApiResponse(responseCode = "403", description = "The user is not an actively linked relative."),
            @ApiResponse(responseCode = "404", description = "Alert not found."),
            @ApiResponse(responseCode = "409", description = "The alert was not acknowledged or is already resolved.")})
    public ResponseEntity<?> resolveInactivityAlert(
            @PathVariable UUID alertId,
            @Valid @RequestBody(required = false) ResolveAlertResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = ResolveInactivityAlertCommandFromResourceAssembler.toCommandFromResource(alertId, principal.userId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                inactivityAlertCommandService.handle(command),
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
