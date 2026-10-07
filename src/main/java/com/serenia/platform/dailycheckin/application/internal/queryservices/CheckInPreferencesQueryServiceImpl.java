package com.serenia.platform.dailycheckin.application.internal.queryservices;

import com.serenia.platform.dailycheckin.application.internal.outboundservices.acl.ExternalIamService;
import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences;
import com.serenia.platform.dailycheckin.domain.model.queries.GetAllCheckInPreferencesQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInPreferencesByOlderAdultIdQuery;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInPreferencesRepository;
import com.serenia.platform.dailycheckin.domain.services.CheckInPreferencesQueryService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service that resolves the preferences of the older adult themselves and the
 * preferences of every older adult for the daily opening.
 */
@Service
public class CheckInPreferencesQueryServiceImpl implements CheckInPreferencesQueryService {

    private static final String NOT_OWN_PREFERENCES = "check.in.preferences.not.owner";

    private final CheckInPreferencesRepository checkInPreferencesRepository;
    private final ExternalIamService externalIamService;

    public CheckInPreferencesQueryServiceImpl(CheckInPreferencesRepository checkInPreferencesRepository,
                                              ExternalIamService externalIamService) {
        this.checkInPreferencesRepository = checkInPreferencesRepository;
        this.externalIamService = externalIamService;
    }

    @Override
    public Result<ImmutablePair<CheckInPreferences, Boolean>, ApplicationError> handle(GetCheckInPreferencesByOlderAdultIdQuery query) {
        if (!query.olderAdultId().equals(query.requesterId()))
            return Result.failure(ApplicationError.forbidden(NOT_OWN_PREFERENCES));
        var preferences = checkInPreferencesRepository.findByOlderAdultId(new OlderAdultId(query.olderAdultId()));
        if (preferences.isEmpty())
            return Result.failure(ApplicationError.notFound("check_in_preferences", query.olderAdultId().toString()));
        var today = externalIamService.fetchToday(query.olderAdultId());
        return Result.success(ImmutablePair.of(preferences.get(), preferences.get().isPausedOn(today)));
    }

    @Override
    public List<CheckInPreferences> handle(GetAllCheckInPreferencesQuery query) {
        return checkInPreferencesRepository.findAll();
    }
}
