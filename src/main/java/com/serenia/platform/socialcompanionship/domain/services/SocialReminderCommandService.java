package com.serenia.platform.socialcompanionship.domain.services;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.SocialReminder;
import com.serenia.platform.socialcompanionship.domain.model.commands.CancelSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.CompleteSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.MarkSocialReminderMissedCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.PostponeSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.PresentSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.ScheduleSocialReminderCommand;

/**
 * Contract of the write operations on social reminders.
 */
public interface SocialReminderCommandService {

    Result<SocialReminder, ApplicationError> handle(ScheduleSocialReminderCommand command);

    Result<Void, ApplicationError> handle(PresentSocialReminderCommand command);

    Result<SocialReminder, ApplicationError> handle(PostponeSocialReminderCommand command);

    Result<SocialReminder, ApplicationError> handle(CompleteSocialReminderCommand command);

    Result<SocialReminder, ApplicationError> handle(CancelSocialReminderCommand command);

    Result<Void, ApplicationError> handle(MarkSocialReminderMissedCommand command);
}
