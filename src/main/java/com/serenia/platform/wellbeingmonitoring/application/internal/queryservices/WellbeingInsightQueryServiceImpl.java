package com.serenia.platform.wellbeingmonitoring.application.internal.queryservices;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.wellbeingmonitoring.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.wellbeingmonitoring.application.internal.outboundservices.acl.ExternalIamService;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.SmallWin;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingSuggestion;
import com.serenia.platform.wellbeingmonitoring.domain.model.queries.GetActiveWellbeingSuggestionsQuery;
import com.serenia.platform.wellbeingmonitoring.domain.model.queries.GetSmallWinsByPeriodQuery;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.wellbeingmonitoring.domain.repositories.WellbeingInsightRepository;
import com.serenia.platform.wellbeingmonitoring.domain.services.WellbeingInsightQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service that resolves the active suggestions and the small wins of a period,
 * converting the dates to instants in the older adult's time zone, after checking the
 * requester's access.
 */
@Service
public class WellbeingInsightQueryServiceImpl implements WellbeingInsightQueryService {

    private static final String RELATIVE_NOT_LINKED = "wellbeing.relative.not.linked";
    private static final String NO_ACCESS = "wellbeing.access.denied";

    private final WellbeingInsightRepository wellbeingInsightRepository;
    private final ExternalIamService externalIamService;
    private final ExternalCareCircleService externalCareCircleService;

    public WellbeingInsightQueryServiceImpl(WellbeingInsightRepository wellbeingInsightRepository,
                                            ExternalIamService externalIamService,
                                            ExternalCareCircleService externalCareCircleService) {
        this.wellbeingInsightRepository = wellbeingInsightRepository;
        this.externalIamService = externalIamService;
        this.externalCareCircleService = externalCareCircleService;
    }

    /** Suggestions are addressed to the relatives, so only actively linked relatives can read them. */
    @Override
    public Result<List<WellbeingSuggestion>, ApplicationError> handle(GetActiveWellbeingSuggestionsQuery query) {
        if (!externalCareCircleService.isActiveRelativeOf(query.requesterId(), query.olderAdultId()))
            return Result.failure(ApplicationError.forbidden(RELATIVE_NOT_LINKED));
        return Result.success(wellbeingInsightRepository.findActiveSuggestionsByOlderAdultId(
                new OlderAdultId(query.olderAdultId())));
    }

    @Override
    public Result<List<SmallWin>, ApplicationError> handle(GetSmallWinsByPeriodQuery query) {
        var isOlderAdult = query.olderAdultId().equals(query.requesterId());
        if (!isOlderAdult && !externalCareCircleService.isActiveRelativeOf(query.requesterId(), query.olderAdultId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));

        var timeZone = externalIamService.fetchTimeZone(query.olderAdultId());
        var from = query.fromDate().atStartOfDay(timeZone).toInstant();
        var to = query.toDate().plusDays(1).atStartOfDay(timeZone).toInstant();
        return Result.success(wellbeingInsightRepository.findSmallWinsByOlderAdultIdBetween(
                new OlderAdultId(query.olderAdultId()), from, to));
    }
}
