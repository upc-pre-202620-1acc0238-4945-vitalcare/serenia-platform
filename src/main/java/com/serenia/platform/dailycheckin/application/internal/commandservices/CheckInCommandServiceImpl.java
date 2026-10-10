package com.serenia.platform.dailycheckin.application.internal.commandservices;

import com.serenia.platform.dailycheckin.application.internal.errors.DailyCheckInErrorMapper;
import com.serenia.platform.dailycheckin.application.internal.outboundservices.acl.ExternalIamService;
import com.serenia.platform.dailycheckin.domain.exceptions.CheckInAlreadyOpenedException;
import com.serenia.platform.dailycheckin.domain.exceptions.DailyCheckInDomainException;
import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.domain.model.commands.AnswerCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.ExpireCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.OpenCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.PromptCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.ResumeDailyCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.services.QuestionSelectionService;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.MoodLevel;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.PositiveActivity;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInPreferencesRepository;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInQuestionRepository;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInRepository;
import com.serenia.platform.dailycheckin.domain.services.CheckInCommandService;
import com.serenia.platform.dailycheckin.domain.services.CheckInPreferencesCommandService;
import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Application service that handles the lifecycle of the daily check-in: opening, prompt,
 * answer and closing as missed or skipped.
 */
@Service
public class CheckInCommandServiceImpl implements CheckInCommandService {

    private static final String WINDOW_ALREADY_CLOSED = "check.in.window.already.closed";
    private static final String QUESTION_CATALOG_EMPTY = "check.in.question.catalog.empty";
    private static final String NOT_CHECK_IN_OWNER = "check.in.not.owner";

    private final CheckInRepository checkInRepository;
    private final CheckInPreferencesRepository checkInPreferencesRepository;
    private final CheckInQuestionRepository checkInQuestionRepository;
    private final CheckInPreferencesCommandService checkInPreferencesCommandService;
    private final ExternalIamService externalIamService;
    private final DomainEventPublisher domainEventPublisher;
    private final QuestionSelectionService questionSelectionService = new QuestionSelectionService();

    public CheckInCommandServiceImpl(CheckInRepository checkInRepository,
                                     CheckInPreferencesRepository checkInPreferencesRepository,
                                     CheckInQuestionRepository checkInQuestionRepository,
                                     CheckInPreferencesCommandService checkInPreferencesCommandService,
                                     ExternalIamService externalIamService,
                                     DomainEventPublisher domainEventPublisher) {
        this.checkInRepository = checkInRepository;
        this.checkInPreferencesRepository = checkInPreferencesRepository;
        this.checkInQuestionRepository = checkInQuestionRepository;
        this.checkInPreferencesCommandService = checkInPreferencesCommandService;
        this.externalIamService = externalIamService;
        this.domainEventPublisher = domainEventPublisher;
    }

    /**
     * Opens the check-in of the older adult's current day if it does not exist yet: picks the
     * question, computes the window from the preferences and, if the previous day ended paused,
     * resumes the questions. A day whose window already closed (e.g. an account registered late
     * in the day) is not opened, so it is never reported as missed.
     */
    @Override
    public Result<CheckIn, ApplicationError> handle(OpenCheckInCommand command) {
        var olderAdultId = new OlderAdultId(command.olderAdultId());
        var preferences = checkInPreferencesRepository.findByOlderAdultId(olderAdultId);
        if (preferences.isEmpty())
            return Result.failure(ApplicationError.notFound("check_in_preferences", command.olderAdultId().toString()));

        var timeZone = externalIamService.fetchTimeZone(command.olderAdultId());
        var today = LocalDate.now(timeZone);
        var existing = checkInRepository.findByOlderAdultIdAndCheckDate(olderAdultId, today);
        if (existing.isPresent()) return Result.success(existing.get());

        var window = preferences.get().windowFor(today, timeZone);
        if (!Instant.now().isBefore(window.deadlineAt()))
            return Result.failure(ApplicationError.businessRuleViolation("check_in", WINDOW_ALREADY_CLOSED));

        var activeQuestions = checkInQuestionRepository.findAllActive();
        if (activeQuestions.isEmpty())
            return Result.failure(ApplicationError.unexpected("check_in", QUESTION_CATALOG_EMPTY));
        var recentQuestionIds = checkInRepository.findRecentQuestionIds(olderAdultId, activeQuestions.size());
        var questionId = questionSelectionService.select(activeQuestions, recentQuestionIds);

        CheckIn opened;
        try {
            opened = saveAndPublish(CheckIn.open(olderAdultId, questionId, today, window));
        } catch (CheckInAlreadyOpenedException e) {
            // Another run opened the same day first: opening is idempotent
            return checkInRepository.findByOlderAdultIdAndCheckDate(olderAdultId, today)
                    .<Result<CheckIn, ApplicationError>>map(Result::success)
                    .orElseGet(() -> Result.failure(DailyCheckInErrorMapper.toApplicationError(e)));
        }

        if (preferences.get().isPausedOn(today.minusDays(1)))
            checkInPreferencesCommandService.handle(new ResumeDailyCheckInCommand(command.olderAdultId()));
        return Result.success(opened);
    }

    /**
     * Prompts a pending check-in only if its questions are not paused; if they are resumed after
     * the reminder time, the prompt is sent on the next run.
     */
    @Override
    public Result<Void, ApplicationError> handle(PromptCheckInCommand command) {
        var checkIn = checkInRepository.findById(new CheckInId(command.checkInId()));
        if (checkIn.isEmpty()) return checkInNotFound(command.checkInId());
        if (isPaused(checkIn.get())) return Result.success(null);
        try {
            checkIn.get().prompt(Instant.now());
            saveAndPublish(checkIn.get());
            return Result.success(null);
        } catch (DailyCheckInDomainException e) {
            return Result.failure(DailyCheckInErrorMapper.toApplicationError(e));
        }
    }

    /** Records the answer after checking that it comes from the older adult of the check-in. */
    @Override
    public Result<CheckIn, ApplicationError> handle(AnswerCheckInCommand command) {
        var checkIn = checkInRepository.findById(new CheckInId(command.checkInId()));
        if (checkIn.isEmpty()) return checkInNotFound(command.checkInId());
        if (!checkIn.get().getOlderAdultId().value().equals(command.olderAdultId()))
            return Result.failure(ApplicationError.forbidden(NOT_CHECK_IN_OWNER));

        var mood = MoodLevel.valueOf(command.mood());
        var positiveActivity = PositiveActivity.fromNullable(command.positiveActivity());
        try {
            checkIn.get().answer(mood, positiveActivity, Instant.now());
            return Result.success(saveAndPublish(checkIn.get()));
        } catch (DailyCheckInDomainException e) {
            return Result.failure(DailyCheckInErrorMapper.toApplicationError(e));
        }
    }

    /**
     * Marks the check-in as missed. If its questions are paused, it stays pending and is marked
     * as skipped only once the local day ends, so no inactivity alert is raised.
     */
    @Override
    public Result<Void, ApplicationError> handle(ExpireCheckInCommand command) {
        var checkIn = checkInRepository.findById(new CheckInId(command.checkInId()));
        if (checkIn.isEmpty()) return checkInNotFound(command.checkInId());
        if (!checkIn.get().isPending()) return Result.success(null);

        var now = Instant.now();
        try {
            if (isPaused(checkIn.get())) {
                var timeZone = externalIamService.fetchTimeZone(checkIn.get().getOlderAdultId().value());
                var endOfDay = checkIn.get().getCheckDate().plusDays(1).atStartOfDay(timeZone).toInstant();
                if (now.isBefore(endOfDay)) return Result.success(null);
                checkIn.get().skip(now);
            } else {
                checkIn.get().expire(now);
            }
            saveAndPublish(checkIn.get());
            return Result.success(null);
        } catch (DailyCheckInDomainException e) {
            return Result.failure(DailyCheckInErrorMapper.toApplicationError(e));
        }
    }

    private boolean isPaused(CheckIn checkIn) {
        return checkInPreferencesRepository.findByOlderAdultId(checkIn.getOlderAdultId())
                .map(preferences -> preferences.isPausedOn(checkIn.getCheckDate()))
                .orElse(false);
    }

    private CheckIn saveAndPublish(CheckIn checkIn) {
        var saved = checkInRepository.save(checkIn);
        var events = List.copyOf(checkIn.domainEvents());
        checkIn.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }

    private static <T> Result<T, ApplicationError> checkInNotFound(UUID checkInId) {
        return Result.failure(ApplicationError.notFound("check_in", checkInId.toString()));
    }
}
