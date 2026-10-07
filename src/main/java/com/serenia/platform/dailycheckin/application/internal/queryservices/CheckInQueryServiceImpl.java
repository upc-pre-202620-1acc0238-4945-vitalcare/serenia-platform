package com.serenia.platform.dailycheckin.application.internal.queryservices;

import com.serenia.platform.dailycheckin.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.dailycheckin.application.internal.outboundservices.acl.ExternalIamService;
import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInHistoryQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInsDueForPromptQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInsPastDeadlineQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetTodayCheckInQuery;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInPreferencesRepository;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInRepository;
import com.serenia.platform.dailycheckin.domain.services.CheckInQueryService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Application service that resolves today's check-in, with the status of the pause, and the
 * history, after checking that the requester is the older adult or an actively linked relative.
 * It also resolves the check-ins to prompt and to close for the periodic jobs.
 */
@Service
public class CheckInQueryServiceImpl implements CheckInQueryService {

    private static final String NO_ACCESS = "check.in.access.denied";

    private final CheckInRepository checkInRepository;
    private final CheckInPreferencesRepository checkInPreferencesRepository;
    private final ExternalIamService externalIamService;
    private final ExternalCareCircleService externalCareCircleService;

    public CheckInQueryServiceImpl(CheckInRepository checkInRepository,
                                   CheckInPreferencesRepository checkInPreferencesRepository,
                                   ExternalIamService externalIamService,
                                   ExternalCareCircleService externalCareCircleService) {
        this.checkInRepository = checkInRepository;
        this.checkInPreferencesRepository = checkInPreferencesRepository;
        this.externalIamService = externalIamService;
        this.externalCareCircleService = externalCareCircleService;
    }

    @Override
    public Result<ImmutablePair<CheckIn, Boolean>, ApplicationError> handle(GetTodayCheckInQuery query) {
        if (!hasAccess(query.requesterId(), query.olderAdultId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));

        var olderAdultId = new OlderAdultId(query.olderAdultId());
        var today = externalIamService.fetchToday(query.olderAdultId());
        var checkIn = checkInRepository.findByOlderAdultIdAndCheckDate(olderAdultId, today);
        if (checkIn.isEmpty())
            return Result.failure(ApplicationError.notFound("check_in", today.toString()));

        var paused = checkInPreferencesRepository.findByOlderAdultId(olderAdultId)
                .map(preferences -> preferences.isPausedOn(today))
                .orElse(false);
        return Result.success(ImmutablePair.of(checkIn.get(), paused));
    }

    @Override
    public Result<List<CheckIn>, ApplicationError> handle(GetCheckInHistoryQuery query) {
        if (!hasAccess(query.requesterId(), query.olderAdultId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));
        return Result.success(checkInRepository.findAllByOlderAdultIdAndCheckDateBetween(
                new OlderAdultId(query.olderAdultId()), query.fromDate(), query.toDate()));
    }

    @Override
    public List<CheckIn> handle(GetCheckInsDueForPromptQuery query) {
        return checkInRepository.findAllPendingDueForPrompt(query.referenceTime());
    }

    @Override
    public List<CheckIn> handle(GetCheckInsPastDeadlineQuery query) {
        return checkInRepository.findAllPendingPastDeadline(query.referenceTime());
    }

    private boolean hasAccess(UUID requesterId, UUID olderAdultId) {
        return olderAdultId.equals(requesterId) || externalCareCircleService.isActiveRelativeOf(requesterId, olderAdultId);
    }
}
