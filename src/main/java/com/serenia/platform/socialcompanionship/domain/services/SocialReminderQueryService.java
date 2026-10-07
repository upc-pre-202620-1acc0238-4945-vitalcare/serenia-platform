package com.serenia.platform.socialcompanionship.domain.services;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.SocialReminder;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetActiveSocialRemindersQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSocialRemindersAwaitingActionQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSocialRemindersDueForPresentationQuery;

import java.util.List;

/**
 * Contract of the read operations on social reminders.
 */
public interface SocialReminderQueryService {

    Result<List<SocialReminder>, ApplicationError> handle(GetActiveSocialRemindersQuery query);

    List<SocialReminder> handle(GetSocialRemindersDueForPresentationQuery query);

    List<SocialReminder> handle(GetSocialRemindersAwaitingActionQuery query);
}
