package com.serenia.platform.socialcompanionship.application.internal.queryservices;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.SocialReminder;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetActiveSocialRemindersQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSocialRemindersAwaitingActionQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSocialRemindersDueForPresentationQuery;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.repositories.SocialReminderRepository;
import com.serenia.platform.socialcompanionship.domain.services.SocialReminderQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service that resolves the active reminders of the older adult themselves, and the
 * reminders to present or to close for the periodic jobs.
 */
@Service
public class SocialReminderQueryServiceImpl implements SocialReminderQueryService {

    private static final String NOT_OWNER = "social.reminder.not.owner";

    private final SocialReminderRepository socialReminderRepository;

    public SocialReminderQueryServiceImpl(SocialReminderRepository socialReminderRepository) {
        this.socialReminderRepository = socialReminderRepository;
    }

    @Override
    public Result<List<SocialReminder>, ApplicationError> handle(GetActiveSocialRemindersQuery query) {
        if (!query.olderAdultId().equals(query.requesterId()))
            return Result.failure(ApplicationError.forbidden(NOT_OWNER));
        return Result.success(socialReminderRepository.findActiveByOlderAdultId(new OlderAdultId(query.olderAdultId())));
    }

    @Override
    public List<SocialReminder> handle(GetSocialRemindersDueForPresentationQuery query) {
        return socialReminderRepository.findAllDueForPresentation(query.referenceTime());
    }

    @Override
    public List<SocialReminder> handle(GetSocialRemindersAwaitingActionQuery query) {
        return socialReminderRepository.findAllAwaitingActionBefore(query.referenceTime());
    }
}
