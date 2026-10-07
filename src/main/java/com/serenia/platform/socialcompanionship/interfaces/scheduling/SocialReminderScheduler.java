package com.serenia.platform.socialcompanionship.interfaces.scheduling;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.socialcompanionship.domain.model.commands.MarkSocialReminderMissedCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.PresentSocialReminderCommand;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSocialRemindersAwaitingActionQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSocialRemindersDueForPresentationQuery;
import com.serenia.platform.socialcompanionship.domain.services.SocialReminderCommandService;
import com.serenia.platform.socialcompanionship.domain.services.SocialReminderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Periodic jobs of the social reminders.
 *
 * <p>Every minute it presents the reminders whose time arrived. Every hour it sends the presented
 * or postponed reminders to be closed as missed, which only happens once their local day ended.</p>
 */
@Component
@Slf4j
public class SocialReminderScheduler {

    private final SocialReminderQueryService socialReminderQueryService;
    private final SocialReminderCommandService socialReminderCommandService;

    public SocialReminderScheduler(SocialReminderQueryService socialReminderQueryService,
                                   SocialReminderCommandService socialReminderCommandService) {
        this.socialReminderQueryService = socialReminderQueryService;
        this.socialReminderCommandService = socialReminderCommandService;
    }

    @Scheduled(fixedDelayString = "${social-companionship.reminders.presentation-interval}")
    public void presentDueReminders() {
        socialReminderQueryService.handle(new GetSocialRemindersDueForPresentationQuery(Instant.now())).forEach(reminder ->
                run("present reminder", reminder.getId().value(),
                        () -> socialReminderCommandService.handle(new PresentSocialReminderCommand(reminder.getId().value()))));
    }

    @Scheduled(fixedRateString = "${social-companionship.reminders.missed-check-interval}")
    public void closeMissedReminders() {
        socialReminderQueryService.handle(new GetSocialRemindersAwaitingActionQuery(Instant.now())).forEach(reminder ->
                run("close reminder", reminder.getId().value(),
                        () -> socialReminderCommandService.handle(new MarkSocialReminderMissedCommand(reminder.getId().value()))));
    }

    /** Runs one command so that a failure with one reminder does not stop the rest of the batch. */
    private void run(String action, UUID id, Supplier<? extends Result<?, ApplicationError>> command) {
        try {
            var result = command.get();
            if (result instanceof Result.Failure<?, ApplicationError> failure)
                log.debug("Could not {} {}: {}", action, id, failure.error().details());
        } catch (RuntimeException e) {
            log.warn("Could not {} {}: {}", action, id, e.getMessage());
        }
    }
}
