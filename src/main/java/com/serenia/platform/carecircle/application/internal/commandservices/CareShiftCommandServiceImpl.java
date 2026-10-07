package com.serenia.platform.carecircle.application.internal.commandservices;

import com.serenia.platform.carecircle.application.internal.errors.CareCircleErrorMapper;
import com.serenia.platform.carecircle.application.internal.outboundservices.acl.ExternalIamService;
import com.serenia.platform.carecircle.domain.exceptions.CareCircleDomainException;
import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.domain.model.aggregates.CareShift;
import com.serenia.platform.carecircle.domain.model.commands.AssignCareShiftCommand;
import com.serenia.platform.carecircle.domain.model.commands.ReassignCareShiftCommand;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareShiftId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.domain.repositories.CareCircleRepository;
import com.serenia.platform.carecircle.domain.repositories.CareShiftRepository;
import com.serenia.platform.carecircle.domain.services.CareShiftCommandService;
import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Application service that handles the assignment and reassignment of care shifts.
 *
 * <p>Checks that the requester has access to the circle and that the relative in charge has
 * an active link, and computes the current day in the older adult's time zone.</p>
 */
@Service
public class CareShiftCommandServiceImpl implements CareShiftCommandService {

    private static final String NO_ACCESS = "care.circle.access.denied";
    private static final String RELATIVE_NOT_LINKED = "care.shift.relative.not.linked";
    private static final String DATE_ALREADY_COVERED = "care.shift.date.already.covered";

    private final CareShiftRepository careShiftRepository;
    private final CareCircleRepository careCircleRepository;
    private final ExternalIamService externalIamService;
    private final DomainEventPublisher domainEventPublisher;

    public CareShiftCommandServiceImpl(CareShiftRepository careShiftRepository,
                                       CareCircleRepository careCircleRepository,
                                       ExternalIamService externalIamService,
                                       DomainEventPublisher domainEventPublisher) {
        this.careShiftRepository = careShiftRepository;
        this.careCircleRepository = careCircleRepository;
        this.externalIamService = externalIamService;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Result<CareShift, ApplicationError> handle(AssignCareShiftCommand command) {
        var careCircleId = new CareCircleId(command.careCircleId());
        var careCircle = careCircleRepository.findById(careCircleId);
        if (careCircle.isEmpty())
            return Result.failure(ApplicationError.notFound("care_circle", command.careCircleId().toString()));

        var relativeId = new RelativeId(command.relativeId());
        if (!careCircle.get().isActiveRelative(relativeId))
            return Result.failure(ApplicationError.forbidden(RELATIVE_NOT_LINKED));
        if (careShiftRepository.existsByCareCircleIdAndShiftDate(careCircleId, command.shiftDate()))
            return Result.failure(ApplicationError.conflict("care_shift", DATE_ALREADY_COVERED));

        try {
            var careShift = CareShift.assign(
                    careCircleId, relativeId, command.shiftDate(), today(careCircle.get()), Instant.now());
            return Result.success(saveAndPublish(careShift));
        } catch (CareCircleDomainException e) {
            return Result.failure(CareCircleErrorMapper.toApplicationError(e));
        }
    }

    @Override
    public Result<CareShift, ApplicationError> handle(ReassignCareShiftCommand command) {
        var careShift = careShiftRepository.findById(new CareShiftId(command.careShiftId()));
        if (careShift.isEmpty())
            return Result.failure(ApplicationError.notFound("care_shift", command.careShiftId().toString()));

        var careCircle = careCircleRepository.findById(careShift.get().getCareCircleId());
        if (careCircle.isEmpty() || !careCircle.get().hasAccess(command.requesterId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));

        var newRelativeId = new RelativeId(command.newRelativeId());
        if (!careCircle.get().isActiveRelative(newRelativeId))
            return Result.failure(ApplicationError.businessRuleViolation("care_shift", RELATIVE_NOT_LINKED));

        try {
            careShift.get().reassignTo(newRelativeId, today(careCircle.get()), Instant.now());
            return Result.success(saveAndPublish(careShift.get()));
        } catch (CareCircleDomainException e) {
            return Result.failure(CareCircleErrorMapper.toApplicationError(e));
        }
    }

    /** Current day of the older adult; falls back to UTC if the time zone cannot be resolved. */
    private LocalDate today(CareCircle careCircle) {
        var timeZone = externalIamService.fetchTimeZone(careCircle.getOlderAdultId().value())
                .orElse(ZoneOffset.UTC);
        return LocalDate.now(timeZone);
    }

    private CareShift saveAndPublish(CareShift careShift) {
        var saved = careShiftRepository.save(careShift);
        var events = List.copyOf(careShift.domainEvents());
        careShift.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }
}
