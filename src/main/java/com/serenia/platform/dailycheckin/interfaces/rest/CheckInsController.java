package com.serenia.platform.dailycheckin.interfaces.rest;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInHistoryQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInQuestionByIdQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetTodayCheckInQuery;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInStatus;
import com.serenia.platform.dailycheckin.domain.services.CheckInCommandService;
import com.serenia.platform.dailycheckin.domain.services.CheckInQueryService;
import com.serenia.platform.dailycheckin.domain.services.CheckInQuestionQueryService;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.AnswerCheckInResource;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.CheckInResource;
import com.serenia.platform.dailycheckin.interfaces.rest.transform.AnswerCheckInCommandFromResourceAssembler;
import com.serenia.platform.dailycheckin.interfaces.rest.transform.CheckInResourceFromEntityAssembler;
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
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

/**
 * REST controller for today's check-in, the history and the answer of the older adult.
 *
 * <p>Queries are allowed to the older adult and to actively linked relatives; the answer,
 * only to the older adult.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/check-ins", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Check-ins", description = "Daily check-in endpoints")
public class CheckInsController {

    private final CheckInCommandService checkInCommandService;
    private final CheckInQueryService checkInQueryService;
    private final CheckInQuestionQueryService checkInQuestionQueryService;

    public CheckInsController(CheckInCommandService checkInCommandService,
                              CheckInQueryService checkInQueryService,
                              CheckInQuestionQueryService checkInQuestionQueryService) {
        this.checkInCommandService = checkInCommandService;
        this.checkInQueryService = checkInQueryService;
        this.checkInQuestionQueryService = checkInQuestionQueryService;
    }

    @GetMapping(value = "/today", params = "olderAdultId")
    @Operation(summary = "Get today's check-in", description = "Gets the check-in of the older adult's current day and whether the questions are paused.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check-in retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = CheckInResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is neither the older adult nor an active relative."),
            @ApiResponse(responseCode = "404", description = "Today's check-in has not been opened yet.")})
    public ResponseEntity<?> getTodayCheckIn(
            @RequestParam UUID olderAdultId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetTodayCheckInQuery(olderAdultId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                checkInQueryService.handle(query),
                today -> CheckInResourceFromEntityAssembler.toResourceFromEntity(
                        today.getLeft(), fetchQuestionText(today.getLeft()), today.getRight()),
                HttpStatus.OK);
    }

    @GetMapping(params = {"olderAdultId", "from", "to"})
    @Operation(summary = "Get check-in history", description = "Gets the check-ins between two dates, inclusive, ordered by date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check-ins retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid date range."),
            @ApiResponse(responseCode = "403", description = "The user is neither the older adult nor an active relative.")})
    public ResponseEntity<?> getCheckInHistory(
            @RequestParam UUID olderAdultId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetCheckInHistoryQuery(olderAdultId, principal.userId(), fromDate, toDate);
        var questionTexts = new HashMap<UUID, String>();
        return ResponseEntityAssembler.toResponseEntityFromResult(
                checkInQueryService.handle(query),
                checkIns -> checkIns.stream()
                        .map(checkIn -> CheckInResourceFromEntityAssembler.toResourceFromEntity(
                                checkIn,
                                questionTexts.computeIfAbsent(checkIn.getQuestionId().value(), _ -> fetchQuestionText(checkIn)),
                                checkIn.getStatus() == CheckInStatus.SKIPPED))
                        .toList(),
                HttpStatus.OK);
    }

    @PostMapping("/{checkInId}/answer")
    @Operation(summary = "Answer check-in", description = "Records the answer of the older adult; it can be sent before the reminder time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check-in answered successfully.",
                    content = @Content(schema = @Schema(implementation = CheckInResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "403", description = "Only the older adult can answer the check-in."),
            @ApiResponse(responseCode = "404", description = "Check-in not found."),
            @ApiResponse(responseCode = "409", description = "The check-in is no longer pending or its deadline passed.")})
    public ResponseEntity<?> answerCheckIn(
            @PathVariable UUID checkInId,
            @Valid @RequestBody AnswerCheckInResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = AnswerCheckInCommandFromResourceAssembler.toCommandFromResource(checkInId, principal.userId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                checkInCommandService.handle(command),
                checkIn -> CheckInResourceFromEntityAssembler.toResourceFromEntity(checkIn, fetchQuestionText(checkIn), false),
                HttpStatus.OK);
    }

    private String fetchQuestionText(CheckIn checkIn) {
        return checkInQuestionQueryService.handle(new GetCheckInQuestionByIdQuery(checkIn.getQuestionId().value()))
                .map(question -> question.getText().value())
                .orElse(null);
    }
}
