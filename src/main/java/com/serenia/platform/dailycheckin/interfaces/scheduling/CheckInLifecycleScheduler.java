package com.serenia.platform.dailycheckin.interfaces.scheduling;

import com.serenia.platform.dailycheckin.domain.model.commands.ExpireCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.OpenCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.PromptCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.queries.GetAllCheckInPreferencesQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInsDueForPromptQuery;
import com.serenia.platform.dailycheckin.domain.model.queries.GetCheckInsPastDeadlineQuery;
import com.serenia.platform.dailycheckin.domain.services.CheckInCommandService;
import com.serenia.platform.dailycheckin.domain.services.CheckInPreferencesQueryService;
import com.serenia.platform.dailycheckin.domain.services.CheckInQueryService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Periodic jobs of the check-in lifecycle.
 *
 * <p>Every hour it opens the current day's check-in of each older adult, which is frequent
 * enough because the earliest allowed reminder time is 06:00. Every minute it prompts the
 * check-ins whose time arrived and closes the ones whose deadline passed.</p>
 */
@Component
@Slf4j
public class CheckInLifecycleScheduler {

    private final CheckInPreferencesQueryService checkInPreferencesQueryService;
    private final CheckInQueryService checkInQueryService;
    private final CheckInCommandService checkInCommandService;

    public CheckInLifecycleScheduler(CheckInPreferencesQueryService checkInPreferencesQueryService,
                                     CheckInQueryService checkInQueryService,
                                     CheckInCommandService checkInCommandService) {
        this.checkInPreferencesQueryService = checkInPreferencesQueryService;
        this.checkInQueryService = checkInQueryService;
        this.checkInCommandService = checkInCommandService;
    }

    @Scheduled(fixedRateString = "${daily-check-in.opening-interval}",
            initialDelayString = "${daily-check-in.opening-initial-delay}")
    public void openTodayCheckIns() {
        checkInPreferencesQueryService.handle(new GetAllCheckInPreferencesQuery()).forEach(preferences -> {
            var olderAdultId = preferences.getOlderAdultId().value();
            run("open the check-in of older adult", olderAdultId,
                    () -> checkInCommandService.handle(new OpenCheckInCommand(olderAdultId)));
        });
    }

    @Scheduled(fixedDelayString = "${daily-check-in.lifecycle-interval}")
    public void promptAndCloseCheckIns() {
        var now = Instant.now();
        checkInQueryService.handle(new GetCheckInsDueForPromptQuery(now)).forEach(checkIn ->
                run("prompt check-in", checkIn.getId().value(),
                        () -> checkInCommandService.handle(new PromptCheckInCommand(checkIn.getId().value()))));
        checkInQueryService.handle(new GetCheckInsPastDeadlineQuery(now)).forEach(checkIn ->
                run("close check-in", checkIn.getId().value(),
                        () -> checkInCommandService.handle(new ExpireCheckInCommand(checkIn.getId().value()))));
    }

    /** Runs one command so that a failure for one older adult does not stop the rest of the batch. */
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
