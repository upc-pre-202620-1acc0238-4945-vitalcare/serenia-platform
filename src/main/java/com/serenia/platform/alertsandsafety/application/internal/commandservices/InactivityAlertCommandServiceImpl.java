package com.serenia.platform.alertsandsafety.application.internal.commandservices;

import com.serenia.platform.alertsandsafety.application.internal.errors.AlertsAndSafetyErrorMapper;
import com.serenia.platform.alertsandsafety.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.alertsandsafety.domain.exceptions.AlertsAndSafetyDomainException;
import com.serenia.platform.alertsandsafety.domain.model.aggregates.InactivityAlert;
import com.serenia.platform.alertsandsafety.domain.model.commands.AcknowledgeInactivityAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.EvaluateInactivityCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.RaiseInactivityAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.ResolveInactivityAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.events.InactivityDetected;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.CheckInId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.RelativeId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.ResolutionNote;
import com.serenia.platform.alertsandsafety.domain.repositories.InactivityAlertRepository;
import com.serenia.platform.alertsandsafety.domain.services.InactivityAlertCommandService;
import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Application service that handles the inactivity alerts: the evaluation of missed check-ins,
 * the raising of the alert and its attention by the relatives.
 */
@Service
public class InactivityAlertCommandServiceImpl implements InactivityAlertCommandService {

    private static final String RELATIVE_NOT_LINKED = "alert.relative.not.linked";

    private final InactivityAlertRepository inactivityAlertRepository;
    private final ExternalCareCircleService externalCareCircleService;
    private final DomainEventPublisher domainEventPublisher;

    public InactivityAlertCommandServiceImpl(InactivityAlertRepository inactivityAlertRepository,
                                             ExternalCareCircleService externalCareCircleService,
                                             DomainEventPublisher domainEventPublisher) {
        this.inactivityAlertRepository = inactivityAlertRepository;
        this.externalCareCircleService = externalCareCircleService;
        this.domainEventPublisher = domainEventPublisher;
    }

    /**
     * Checks whether the check-in already has an alert and how many linked relatives the older
     * adult has; when it is inactivity to be alerted, publishes {@link InactivityDetected}, whose
     * handler raises the alert inside this same transaction.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<Boolean, ApplicationError> handle(EvaluateInactivityCommand command) {
        var alreadyRaised = inactivityAlertRepository.existsByCheckInId(new CheckInId(command.checkInId()));
        var activeRelativeCount = externalCareCircleService.fetchActiveRelativeIds(command.olderAdultId()).size();
        if (!InactivityAlert.evaluate(alreadyRaised, activeRelativeCount)) return Result.success(false);

        domainEventPublisher.publish(List.of(new InactivityDetected(
                command.olderAdultId(), command.checkInId(), command.checkDate(), Instant.now())));
        return Result.success(true);
    }

    @Override
    @Transactional
    public Result<InactivityAlert, ApplicationError> handle(RaiseInactivityAlertCommand command) {
        var alert = InactivityAlert.raise(new OlderAdultId(command.olderAdultId()), new CheckInId(command.checkInId()), Instant.now());
        try {
            return Result.success(saveAndPublish(alert));
        } catch (AlertsAndSafetyDomainException e) {
            return Result.failure(AlertsAndSafetyErrorMapper.toApplicationError(e));
        }
    }

    @Override
    public Result<InactivityAlert, ApplicationError> handle(AcknowledgeInactivityAlertCommand command) {
        return change(command.alertId(), command.relativeId(),
                alert -> alert.acknowledge(new RelativeId(command.relativeId()), Instant.now()));
    }

    @Override
    public Result<InactivityAlert, ApplicationError> handle(ResolveInactivityAlertCommand command) {
        var resolutionNote = ResolutionNote.fromNullable(command.resolutionNote());
        return change(command.alertId(), command.relativeId(),
                alert -> alert.resolve(new RelativeId(command.relativeId()), resolutionNote, Instant.now()));
    }

    /**
     * Loads the alert, checks that the relative is actively linked to the older adult, applies
     * the change and persists it.
     */
    private Result<InactivityAlert, ApplicationError> change(UUID alertId, UUID relativeId, Consumer<InactivityAlert> change) {
        var alert = inactivityAlertRepository.findById(new AlertId(alertId));
        if (alert.isEmpty())
            return Result.failure(ApplicationError.notFound("alert", alertId.toString()));
        if (!externalCareCircleService.isActiveRelativeOf(relativeId, alert.get().getOlderAdultId().value()))
            return Result.failure(ApplicationError.forbidden(RELATIVE_NOT_LINKED));
        try {
            change.accept(alert.get());
            return Result.success(saveAndPublish(alert.get()));
        } catch (AlertsAndSafetyDomainException e) {
            return Result.failure(AlertsAndSafetyErrorMapper.toApplicationError(e));
        }
    }

    private InactivityAlert saveAndPublish(InactivityAlert alert) {
        var saved = inactivityAlertRepository.save(alert);
        var events = List.copyOf(alert.domainEvents());
        alert.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }
}
