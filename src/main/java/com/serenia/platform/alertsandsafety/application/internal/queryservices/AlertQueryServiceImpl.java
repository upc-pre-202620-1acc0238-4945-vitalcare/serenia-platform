package com.serenia.platform.alertsandsafety.application.internal.queryservices;

import com.serenia.platform.alertsandsafety.application.internal.outboundservices.acl.ExternalCareCircleService;
import com.serenia.platform.alertsandsafety.domain.model.aggregates.Alert;
import com.serenia.platform.alertsandsafety.domain.model.queries.GetAlertByIdQuery;
import com.serenia.platform.alertsandsafety.domain.model.queries.GetAlertsByOlderAdultIdQuery;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.alertsandsafety.domain.repositories.EmergencyAlertRepository;
import com.serenia.platform.alertsandsafety.domain.repositories.InactivityAlertRepository;
import com.serenia.platform.alertsandsafety.domain.services.AlertQueryService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Application service that resolves the history and the detail of alerts, combining both types
 * ordered by date, after checking that the requester is the older adult or an actively linked relative.
 */
@Service
public class AlertQueryServiceImpl implements AlertQueryService {

    private static final String NO_ACCESS = "alert.access.denied";

    private final EmergencyAlertRepository emergencyAlertRepository;
    private final InactivityAlertRepository inactivityAlertRepository;
    private final ExternalCareCircleService externalCareCircleService;

    public AlertQueryServiceImpl(EmergencyAlertRepository emergencyAlertRepository,
                                 InactivityAlertRepository inactivityAlertRepository,
                                 ExternalCareCircleService externalCareCircleService) {
        this.emergencyAlertRepository = emergencyAlertRepository;
        this.inactivityAlertRepository = inactivityAlertRepository;
        this.externalCareCircleService = externalCareCircleService;
    }

    @Override
    public Result<List<Alert>, ApplicationError> handle(GetAlertsByOlderAdultIdQuery query) {
        if (!hasAccess(query.requesterId(), query.olderAdultId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));
        var olderAdultId = new OlderAdultId(query.olderAdultId());
        return Result.success(Stream.<Alert>concat(
                        emergencyAlertRepository.findAllByOlderAdultId(olderAdultId).stream(),
                        inactivityAlertRepository.findAllByOlderAdultId(olderAdultId).stream())
                .sorted(Comparator.comparing(Alert::getTriggeredAt).reversed())
                .toList());
    }

    @Override
    public Result<Alert, ApplicationError> handle(GetAlertByIdQuery query) {
        var alertId = new AlertId(query.alertId());
        var alert = emergencyAlertRepository.findById(alertId).<Alert>map(found -> found)
                .or(() -> inactivityAlertRepository.findById(alertId));
        if (alert.isEmpty())
            return Result.failure(ApplicationError.notFound("alert", query.alertId().toString()));
        if (!hasAccess(query.requesterId(), alert.get().getOlderAdultId().value()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));
        return Result.success(alert.get());
    }

    private boolean hasAccess(UUID requesterId, UUID olderAdultId) {
        return olderAdultId.equals(requesterId) || externalCareCircleService.isActiveRelativeOf(requesterId, olderAdultId);
    }
}
