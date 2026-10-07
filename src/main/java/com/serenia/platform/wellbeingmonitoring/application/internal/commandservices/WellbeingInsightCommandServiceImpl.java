package com.serenia.platform.wellbeingmonitoring.application.internal.commandservices;

import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.wellbeingmonitoring.application.internal.errors.WellbeingMonitoringErrorMapper;
import com.serenia.platform.wellbeingmonitoring.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.wellbeingmonitoring.application.internal.outboundservices.acl.ExternalDailyCheckInService;
import com.serenia.platform.wellbeingmonitoring.domain.exceptions.WellbeingMonitoringDomainException;
import com.serenia.platform.wellbeingmonitoring.domain.model.aggregates.WellbeingInsight;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.DismissWellbeingSuggestionCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.EvaluateWellbeingPatternCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.IssueWellbeingSuggestionCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.RecordSmallWinCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.SmallWin;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingSuggestion;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.CheckInId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.MoodLevel;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.RelativeId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SmallWinDescription;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SuggestionMessage;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.WellbeingPatternId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.WellbeingSuggestionId;
import com.serenia.platform.wellbeingmonitoring.domain.repositories.WellbeingInsightRepository;
import com.serenia.platform.wellbeingmonitoring.domain.services.WellbeingInsightCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Application service that handles the write operations of the Wellbeing Monitoring context.
 *
 * <p>Evaluation runs in its own transaction: the suggestion and the small win it triggers are
 * created synchronously inside it, and a failure never affects the answered check-in.</p>
 */
@Service
public class WellbeingInsightCommandServiceImpl implements WellbeingInsightCommandService {

    /** Days before the evaluated one whose moods are taken into account. */
    private static final int EVALUATED_PREVIOUS_DAYS = 7;

    private static final String SUGGESTION_MESSAGE =
            "Lleva %d días seguidos con el ánimo bajo. Puede ser un buen momento para llamarle o visitarle.";
    private static final String GOOD_DAY_DESCRIPTION = "Tuvo un buen día";
    private static final String RELATIVE_NOT_LINKED = "wellbeing.relative.not.linked";

    private final WellbeingInsightRepository wellbeingInsightRepository;
    private final ExternalDailyCheckInService externalDailyCheckInService;
    private final ExternalCareCircleService externalCareCircleService;
    private final DomainEventPublisher domainEventPublisher;

    public WellbeingInsightCommandServiceImpl(WellbeingInsightRepository wellbeingInsightRepository,
                                              ExternalDailyCheckInService externalDailyCheckInService,
                                              ExternalCareCircleService externalCareCircleService,
                                              DomainEventPublisher domainEventPublisher) {
        this.wellbeingInsightRepository = wellbeingInsightRepository;
        this.externalDailyCheckInService = externalDailyCheckInService;
        this.externalCareCircleService = externalCareCircleService;
        this.domainEventPublisher = domainEventPublisher;
    }

    /** Evaluates an answered check-in with the moods of the seven previous days. */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<Void, ApplicationError> handle(EvaluateWellbeingPatternCommand command) {
        var recentMoods = externalDailyCheckInService.fetchMoods(command.olderAdultId(),
                command.checkDate().minusDays(EVALUATED_PREVIOUS_DAYS), command.checkDate().minusDays(1));
        return change(command.olderAdultId(), insight -> {
            insight.evaluate(new CheckInId(command.checkInId()), command.checkDate(), MoodLevel.valueOf(command.mood()),
                    command.positiveActivity(), recentMoods, Instant.now());
            return null;
        });
    }

    /** Issues the suggestion of a pattern, writing the message with the number of days of the pattern. */
    @Override
    @Transactional
    public Result<WellbeingSuggestion, ApplicationError> handle(IssueWellbeingSuggestionCommand command) {
        return change(command.olderAdultId(), insight -> {
            var pattern = insight.getLatestDiscomfortPattern();
            var days = pattern == null ? WellbeingInsight.DISCOMFORT_STREAK_DAYS : pattern.getConsecutiveDays();
            return insight.issueSuggestion(new WellbeingPatternId(command.patternId()),
                    new SuggestionMessage(SUGGESTION_MESSAGE.formatted(days)), Instant.now());
        });
    }

    /** Records a small win with the reported positive activity or, if there was none, a description of the good day. */
    @Override
    @Transactional
    public Result<SmallWin, ApplicationError> handle(RecordSmallWinCommand command) {
        var description = command.positiveActivity() == null || command.positiveActivity().isBlank()
                ? GOOD_DAY_DESCRIPTION
                : command.positiveActivity();
        return change(command.olderAdultId(), insight -> insight.recordSmallWin(
                new CheckInId(command.checkInId()), new SmallWinDescription(description), Instant.now()));
    }

    /** Dismisses a suggestion after checking that the relative has an active link with the older adult. */
    @Override
    @Transactional
    public Result<WellbeingSuggestion, ApplicationError> handle(DismissWellbeingSuggestionCommand command) {
        if (!externalCareCircleService.isActiveRelativeOf(command.relativeId(), command.olderAdultId()))
            return Result.failure(ApplicationError.forbidden(RELATIVE_NOT_LINKED));
        return change(command.olderAdultId(), insight -> insight.dismissSuggestion(
                new WellbeingSuggestionId(command.suggestionId()), new RelativeId(command.relativeId()), Instant.now()));
    }

    /** Loads the aggregate, applies the change, persists it and publishes the accumulated events. */
    private <T> Result<T, ApplicationError> change(UUID olderAdultId, Function<WellbeingInsight, T> operation) {
        var insight = wellbeingInsightRepository.findByOlderAdultId(new OlderAdultId(olderAdultId));
        try {
            var outcome = operation.apply(insight);
            wellbeingInsightRepository.save(insight);
            var events = List.copyOf(insight.domainEvents());
            insight.clearDomainEvents();
            domainEventPublisher.publish(events);
            return Result.success(outcome);
        } catch (WellbeingMonitoringDomainException e) {
            return Result.failure(WellbeingMonitoringErrorMapper.toApplicationError(e));
        }
    }
}
