package com.serenia.platform.dailycheckin.application.internal.commandservices;

import com.serenia.platform.dailycheckin.application.internal.errors.DailyCheckInErrorMapper;
import com.serenia.platform.dailycheckin.application.internal.outboundservices.acl.ExternalIamService;
import com.serenia.platform.dailycheckin.domain.exceptions.DailyCheckInDomainException;
import com.serenia.platform.dailycheckin.domain.exceptions.DailyCheckInNotPausedException;
import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckIn;
import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences;
import com.serenia.platform.dailycheckin.domain.model.commands.ActivateDailyPauseCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.DisableSimplifiedModeCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.EnableSimplifiedModeCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.InitializeCheckInPreferencesCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.ResumeDailyCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.ScheduleCheckInCommand;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.ReminderTime;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInPreferencesRepository;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInRepository;
import com.serenia.platform.dailycheckin.domain.services.CheckInPreferencesCommandService;
import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Application service that handles the write operations on the check-in preferences.
 *
 * <p>The pause works on the older adult's current day, computed in their time zone. Resuming
 * the questions is the only operation that changes both aggregates in one transaction, because
 * the older adult's decision must take effect immediately.</p>
 */
@Service
public class CheckInPreferencesCommandServiceImpl implements CheckInPreferencesCommandService {

    private final CheckInPreferencesRepository checkInPreferencesRepository;
    private final CheckInRepository checkInRepository;
    private final ExternalIamService externalIamService;
    private final DomainEventPublisher domainEventPublisher;

    public CheckInPreferencesCommandServiceImpl(CheckInPreferencesRepository checkInPreferencesRepository,
                                                CheckInRepository checkInRepository,
                                                ExternalIamService externalIamService,
                                                DomainEventPublisher domainEventPublisher) {
        this.checkInPreferencesRepository = checkInPreferencesRepository;
        this.checkInRepository = checkInRepository;
        this.externalIamService = externalIamService;
        this.domainEventPublisher = domainEventPublisher;
    }

    /** Creates the preferences without duplicating them if the older adult already has some. */
    @Override
    public Result<CheckInPreferences, ApplicationError> handle(InitializeCheckInPreferencesCommand command) {
        var olderAdultId = new OlderAdultId(command.olderAdultId());
        var existing = checkInPreferencesRepository.findByOlderAdultId(olderAdultId);
        if (existing.isPresent()) return Result.success(existing.get());
        return Result.success(saveAndPublish(CheckInPreferences.initialize(olderAdultId, Instant.now())));
    }

    @Override
    public Result<CheckInPreferences, ApplicationError> handle(ScheduleCheckInCommand command) {
        var reminderTime = new ReminderTime(command.reminderTime());
        return update(command.olderAdultId(), preferences -> preferences.schedule(reminderTime, Instant.now()));
    }

    @Override
    public Result<CheckInPreferences, ApplicationError> handle(ActivateDailyPauseCommand command) {
        var today = externalIamService.fetchToday(command.olderAdultId());
        return update(command.olderAdultId(), preferences -> preferences.activateDailyPause(today, Instant.now()));
    }

    /**
     * If today is paused, lifts the pause and, if the check-in deadline already passed, gives it
     * a new deadline of the same length from now, never beyond the end of the local day. If today
     * is not paused but the previous day ended paused, registers the resumption because a new day
     * started. Otherwise rejects the operation.
     */
    @Override
    @Transactional
    public Result<CheckInPreferences, ApplicationError> handle(ResumeDailyCheckInCommand command) {
        var preferences = checkInPreferencesRepository.findByOlderAdultId(new OlderAdultId(command.olderAdultId()));
        if (preferences.isEmpty()) return preferencesNotFound(command.olderAdultId());

        var timeZone = externalIamService.fetchTimeZone(command.olderAdultId());
        var today = LocalDate.now(timeZone);
        var now = Instant.now();
        try {
            if (preferences.get().isPausedOn(today)) {
                preferences.get().resumeToday(today, now);
                extendExpiredDeadline(preferences.get(), today, timeZone, now);
            } else if (!preferences.get().resumeAfterPausedDay(today)) {
                throw new DailyCheckInNotPausedException();
            }
            return Result.success(saveAndPublish(preferences.get()));
        } catch (DailyCheckInDomainException e) {
            return Result.failure(DailyCheckInErrorMapper.toApplicationError(e));
        }
    }

    @Override
    public Result<CheckInPreferences, ApplicationError> handle(EnableSimplifiedModeCommand command) {
        return update(command.olderAdultId(), preferences -> preferences.enableSimplifiedMode(Instant.now()));
    }

    @Override
    public Result<CheckInPreferences, ApplicationError> handle(DisableSimplifiedModeCommand command) {
        return update(command.olderAdultId(), preferences -> preferences.disableSimplifiedMode(Instant.now()));
    }

    private void extendExpiredDeadline(CheckInPreferences preferences, LocalDate today, ZoneId timeZone, Instant now) {
        checkInRepository.findByOlderAdultIdAndCheckDate(preferences.getOlderAdultId(), today)
                .filter(CheckIn::isPending)
                .filter(checkIn -> !now.isBefore(checkIn.getWindow().deadlineAt()))
                .ifPresent(checkIn -> {
                    var endOfDay = today.plusDays(1).atStartOfDay(timeZone).toInstant();
                    var extended = now.plus(preferences.getTimeLimit().toDuration());
                    var newDeadline = extended.isBefore(endOfDay) ? extended : endOfDay;
                    if (newDeadline.isAfter(checkIn.getWindow().deadlineAt())) {
                        checkIn.extendDeadline(newDeadline);
                        checkInRepository.save(checkIn);
                    }
                });
    }

    private Result<CheckInPreferences, ApplicationError> update(UUID olderAdultId, Consumer<CheckInPreferences> change) {
        var preferences = checkInPreferencesRepository.findByOlderAdultId(new OlderAdultId(olderAdultId));
        if (preferences.isEmpty()) return preferencesNotFound(olderAdultId);
        try {
            change.accept(preferences.get());
            return Result.success(saveAndPublish(preferences.get()));
        } catch (DailyCheckInDomainException e) {
            return Result.failure(DailyCheckInErrorMapper.toApplicationError(e));
        }
    }

    private CheckInPreferences saveAndPublish(CheckInPreferences preferences) {
        var saved = checkInPreferencesRepository.save(preferences);
        var events = List.copyOf(preferences.domainEvents());
        preferences.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }

    private static Result<CheckInPreferences, ApplicationError> preferencesNotFound(UUID olderAdultId) {
        return Result.failure(ApplicationError.notFound("check_in_preferences", olderAdultId.toString()));
    }
}
