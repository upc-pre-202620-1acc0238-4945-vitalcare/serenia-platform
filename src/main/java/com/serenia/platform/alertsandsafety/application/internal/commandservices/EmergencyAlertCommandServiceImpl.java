package com.serenia.platform.alertsandsafety.application.internal.commandservices;

import com.serenia.platform.alertsandsafety.application.internal.errors.AlertsAndSafetyErrorMapper;
import com.serenia.platform.alertsandsafety.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.alertsandsafety.domain.exceptions.AlertsAndSafetyDomainException;
import com.serenia.platform.alertsandsafety.domain.model.aggregates.EmergencyAlert;
import com.serenia.platform.alertsandsafety.domain.model.commands.AcknowledgeEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.ConfirmEmergencyAlertDeliveryCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.DispatchEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.ResolveEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.commands.TriggerEmergencyAlertCommand;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.RelativeId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.ResolutionNote;
import com.serenia.platform.alertsandsafety.domain.repositories.EmergencyAlertRepository;
import com.serenia.platform.alertsandsafety.domain.services.EmergencyAlertCommandService;
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
 * Application service that handles the emergency alerts: their triggering, dispatch, delivery
 * confirmation and attention by the relatives.
 */
@Service
public class EmergencyAlertCommandServiceImpl implements EmergencyAlertCommandService {

    private static final String RELATIVE_NOT_LINKED = "alert.relative.not.linked";

    private final EmergencyAlertRepository emergencyAlertRepository;
    private final ExternalCareCircleService externalCareCircleService;
    private final DomainEventPublisher domainEventPublisher;

    public EmergencyAlertCommandServiceImpl(EmergencyAlertRepository emergencyAlertRepository,
                                            ExternalCareCircleService externalCareCircleService,
                                            DomainEventPublisher domainEventPublisher) {
        this.emergencyAlertRepository = emergencyAlertRepository;
        this.externalCareCircleService = externalCareCircleService;
        this.domainEventPublisher = domainEventPublisher;
    }

    /**
     * Records the emergency of the older adult. Its dispatch runs right after, in its own
     * transaction, so the emergency stays recorded even if the dispatch fails; the alert is then
     * reloaded so the answer to the older adult already reflects the outcome.
     */
    @Override
    public Result<EmergencyAlert, ApplicationError> handle(TriggerEmergencyAlertCommand command) {
        var alert = EmergencyAlert.trigger(new OlderAdultId(command.olderAdultId()), Instant.now());
        saveAndPublish(alert);
        return Result.success(emergencyAlertRepository.findById(alert.getId()).orElse(alert));
    }

    /** Notifies the emergency to the relatives actively linked to the older adult at this moment. */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<EmergencyAlert, ApplicationError> handle(DispatchEmergencyAlertCommand command) {
        return change(command.alertId(), null, alert -> {
            var relativeIds = externalCareCircleService.fetchActiveRelativeIds(alert.getOlderAdultId().value()).stream()
                    .map(RelativeId::new)
                    .toList();
            alert.dispatch(relativeIds, Instant.now());
        });
    }

    /** Marks the delivery inside the transaction of the dispatch. */
    @Override
    @Transactional
    public Result<EmergencyAlert, ApplicationError> handle(ConfirmEmergencyAlertDeliveryCommand command) {
        return change(command.alertId(), null, alert -> alert.confirmDelivery(Instant.now()));
    }

    @Override
    public Result<EmergencyAlert, ApplicationError> handle(AcknowledgeEmergencyAlertCommand command) {
        return change(command.alertId(), command.relativeId(),
                alert -> alert.acknowledge(new RelativeId(command.relativeId()), Instant.now()));
    }

    @Override
    public Result<EmergencyAlert, ApplicationError> handle(ResolveEmergencyAlertCommand command) {
        var resolutionNote = ResolutionNote.fromNullable(command.resolutionNote());
        return change(command.alertId(), command.relativeId(),
                alert -> alert.resolve(new RelativeId(command.relativeId()), resolutionNote, Instant.now()));
    }

    /**
     * Loads the alert, checks that the relative, when there is one, is actively linked to the
     * older adult, applies the change and persists it.
     */
    private Result<EmergencyAlert, ApplicationError> change(UUID alertId, UUID relativeId, Consumer<EmergencyAlert> change) {
        var alert = emergencyAlertRepository.findById(new AlertId(alertId));
        if (alert.isEmpty())
            return Result.failure(ApplicationError.notFound("alert", alertId.toString()));
        if (relativeId != null && !externalCareCircleService.isActiveRelativeOf(relativeId, alert.get().getOlderAdultId().value()))
            return Result.failure(ApplicationError.forbidden(RELATIVE_NOT_LINKED));
        try {
            change.accept(alert.get());
            return Result.success(saveAndPublish(alert.get()));
        } catch (AlertsAndSafetyDomainException e) {
            return Result.failure(AlertsAndSafetyErrorMapper.toApplicationError(e));
        }
    }

    private EmergencyAlert saveAndPublish(EmergencyAlert alert) {
        var saved = emergencyAlertRepository.save(alert);
        var events = List.copyOf(alert.domainEvents());
        alert.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }
}
