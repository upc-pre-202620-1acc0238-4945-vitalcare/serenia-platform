package com.serenia.platform.wellbeingmonitoring.interfaces.rest;

import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.serenia.platform.wellbeingmonitoring.domain.model.queries.GetSmallWinsByPeriodQuery;
import com.serenia.platform.wellbeingmonitoring.domain.services.WellbeingInsightQueryService;
import com.serenia.platform.wellbeingmonitoring.interfaces.rest.transform.SmallWinResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST controller for the small wins of a period, available to the older adult and to their
 * linked relatives.
 */
@RestController
@RequestMapping(value = "/api/v1/small-wins", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Small Wins", description = "Small win endpoints")
public class SmallWinsController {

    private final WellbeingInsightQueryService wellbeingInsightQueryService;

    public SmallWinsController(WellbeingInsightQueryService wellbeingInsightQueryService) {
        this.wellbeingInsightQueryService = wellbeingInsightQueryService;
    }

    @GetMapping(params = {"olderAdultId", "from", "to"})
    @Operation(summary = "Get small wins", description = "Gets the small wins between two dates, inclusive, in the older adult's time zone.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Small wins retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid date range."),
            @ApiResponse(responseCode = "403", description = "The user is neither the older adult nor an active relative.")})
    public ResponseEntity<?> getSmallWins(
            @RequestParam UUID olderAdultId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetSmallWinsByPeriodQuery(olderAdultId, principal.userId(), fromDate, toDate);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                wellbeingInsightQueryService.handle(query),
                smallWins -> smallWins.stream()
                        .map(smallWin -> SmallWinResourceFromEntityAssembler.toResourceFromEntity(olderAdultId, smallWin))
                        .toList(),
                HttpStatus.OK);
    }
}
