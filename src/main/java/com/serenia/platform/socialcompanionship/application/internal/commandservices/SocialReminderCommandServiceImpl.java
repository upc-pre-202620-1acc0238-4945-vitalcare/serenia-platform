package com.serenia.platform.socialcompanionship.application.internal.commandservices;

import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.application.internal.errors.SocialCompanionshipErrorMapper;
import com.serenia.platform.socialcompanionship.application.internal.outboundservices.acl.ExternalIamService;
import com.serenia.platform.socialcompanionship.domain.exceptions.SocialCompanionshipDomainException;
import com.serenia.platform.socialcompanionship.domain.model.aggregates.SocialReminder;
import com.serenia.platform.socialcompanionship.domain.model.commands.CancelSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.CompleteSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.MarkSocialReminderMissedCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.PostponeSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.PresentSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.ScheduleSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderDescription;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderTitle;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.SocialReminderId;
import com.serenia.platform.socialcompanionship.domain.repositories.SocialReminderRepository;
import com.serenia.platform.socialcompanionship.domain.services.SocialReminderCommandService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Application service that handles the lifecycle of social reminders. Local dates and times are
 * interpreted in the older adult's time zone.
 */
@Service
public class SocialReminderCommandServiceImpl implements SocialReminderCommandService {

    private static final String REMIND_AT_REQUIRED = "social.reminder.remind.at.required";
    private static final String NOT_OWNER = "social.reminder.not.owner";

    private final SocialReminderRepository socialReminderRepository;
    private final ExternalIamService externalIamService;
    private final DomainEventPublisher domainEventPublisher;

    public SocialReminderCommandServiceImpl(SocialReminderRepository socialReminderRepository,
                                            ExternalIamService externalIamService,
                                            DomainEventPublisher domainEventPublisher) {
        this.socialReminderRepository = socialReminderRepository;
        this.externalIamService = externalIamService;
        this.domainEventPublisher = domainEventPublisher;
    }

    /** Interprets the local date and time in the older adult's time zone and schedules the reminder. */
    @Override
    public Result<SocialReminder, ApplicationError> handle(ScheduleSocialReminderCommand command) {
        if (command.remindAt() == null) throw new IllegalArgumentException(REMIND_AT_REQUIRED);
        var timeZone = externalIamService.fetchTimeZone(command.olderAdultId());
        var reminder = SocialReminder.schedule(
                new OlderAdultId(command.olderAdultId()),
                new ReminderTitle(command.title()),
                ReminderDescription.fromNullable(command.description()),
                command.remindAt().atZone(timeZone).toInstant(),
                Instant.now());
        return Result.success(saveAndPublish(reminder));
    }

    @Override
    public Result<Void, ApplicationError> handle(PresentSocialReminderCommand command) {
        return change(command.socialReminderId(), Optional.empty(), reminder -> reminder.present(Instant.now()))
                .map(_ -> null);
    }

    /** Postpones a presented reminder within the older adult's current local day. */
    @Override
    public Result<SocialReminder, ApplicationError> handle(PostponeSocialReminderCommand command) {
        var timeZone = externalIamService.fetchTimeZone(command.olderAdultId());
        var endOfDay = LocalDate.now(timeZone).plusDays(1).atStartOfDay(timeZone).toInstant();
        return change(command.socialReminderId(), Optional.of(command.olderAdultId()),
                reminder -> reminder.postpone(Instant.now(), endOfDay));
    }

    @Override
    public Result<SocialReminder, ApplicationError> handle(CompleteSocialReminderCommand command) {
        return change(command.socialReminderId(), Optional.of(command.olderAdultId()),
                reminder -> reminder.complete(Instant.now()));
    }

    @Override
    public Result<SocialReminder, ApplicationError> handle(CancelSocialReminderCommand command) {
        return change(command.socialReminderId(), Optional.of(command.olderAdultId()),
                reminder -> reminder.cancel(Instant.now()));
    }

    /** Closes the reminder as missed only if its local day already ended; otherwise makes no changes. */
    @Override
    public Result<Void, ApplicationError> handle(MarkSocialReminderMissedCommand command) {
        var reminder = socialReminderRepository.findById(new SocialReminderId(command.socialReminderId()));
        if (reminder.isEmpty()) return reminderNotFound(command.socialReminderId());
        if (!reminder.get().isAwaitingAction()) return Result.success(null);

        var timeZone = externalIamService.fetchTimeZone(reminder.get().getOlderAdultId().value());
        var reminderDay = reminder.get().getRemindAt().atZone(timeZone).toLocalDate();
        var now = Instant.now();
        if (now.isBefore(reminderDay.plusDays(1).atStartOfDay(timeZone).toInstant())) return Result.success(null);

        reminder.get().markAsMissed(now);
        saveAndPublish(reminder.get());
        return Result.success(null);
    }

    /**
     * Loads the reminder, checks that it belongs to the requesting older adult when there is one,
     * applies the change and persists it.
     */
    private Result<SocialReminder, ApplicationError> change(UUID socialReminderId, Optional<UUID> requesterId,
                                                            Consumer<SocialReminder> change) {
        var reminder = socialReminderRepository.findById(new SocialReminderId(socialReminderId));
        if (reminder.isEmpty()) return reminderNotFound(socialReminderId);
        if (requesterId.isPresent() && !reminder.get().belongsTo(requesterId.get()))
            return Result.failure(ApplicationError.forbidden(NOT_OWNER));
        try {
            change.accept(reminder.get());
            return Result.success(saveAndPublish(reminder.get()));
        } catch (SocialCompanionshipDomainException e) {
            return Result.failure(SocialCompanionshipErrorMapper.toApplicationError(e));
        }
    }

    private SocialReminder saveAndPublish(SocialReminder reminder) {
        var saved = socialReminderRepository.save(reminder);
        var events = List.copyOf(reminder.domainEvents());
        reminder.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }

    private static <T> Result<T, ApplicationError> reminderNotFound(UUID socialReminderId) {
        return Result.failure(ApplicationError.notFound("social_reminder", socialReminderId.toString()));
    }
}
