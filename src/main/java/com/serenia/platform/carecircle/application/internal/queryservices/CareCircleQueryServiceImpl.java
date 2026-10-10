package com.serenia.platform.carecircle.application.internal.queryservices;

import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.domain.model.entities.FamilyLink;
import com.serenia.platform.carecircle.domain.model.entities.InvitationCode;
import com.serenia.platform.carecircle.domain.model.queries.GetActiveFamilyLinksByCareCircleIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetActiveFamilyLinksByRelativeIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetCareCircleByIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetCareCircleByOlderAdultIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetDueInvitationCodesQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetPendingInvitationCodesByCareCircleIdQuery;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.domain.repositories.CareCircleRepository;
import com.serenia.platform.carecircle.domain.services.CareCircleQueryService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service that resolves the queries on care circles, pending and due codes,
 * and active links, checking the requester's access when the query comes from a user.
 */
@Service
public class CareCircleQueryServiceImpl implements CareCircleQueryService {

    private static final String NO_ACCESS = "care.circle.access.denied";

    private final CareCircleRepository careCircleRepository;

    public CareCircleQueryServiceImpl(CareCircleRepository careCircleRepository) {
        this.careCircleRepository = careCircleRepository;
    }

    @Override
    public Result<CareCircle, ApplicationError> handle(GetCareCircleByIdQuery query) {
        return findAccessibleCareCircle(query.careCircleId(), query.requesterId());
    }

    @Override
    public Optional<CareCircle> handle(GetCareCircleByOlderAdultIdQuery query) {
        return careCircleRepository.findByOlderAdultId(new OlderAdultId(query.olderAdultId()));
    }

    @Override
    public Result<List<InvitationCode>, ApplicationError> handle(GetPendingInvitationCodesByCareCircleIdQuery query) {
        var now = Instant.now();
        return findAccessibleCareCircle(query.careCircleId(), query.requesterId())
                .map(careCircle -> careCircle.redeemableInvitationCodes(now));
    }

    @Override
    public List<CareCircle> handle(GetDueInvitationCodesQuery query) {
        return careCircleRepository.findDueInvitationCodes(query.referenceTime());
    }

    @Override
    public Result<List<FamilyLink>, ApplicationError> handle(GetActiveFamilyLinksByCareCircleIdQuery query) {
        return findAccessibleCareCircle(query.careCircleId(), query.requesterId())
                .map(CareCircle::activeFamilyLinks);
    }

    @Override
    public List<CareCircle> handle(GetActiveFamilyLinksByRelativeIdQuery query) {
        return careCircleRepository.findAllByActiveRelativeId(new RelativeId(query.relativeId()));
    }

    private Result<CareCircle, ApplicationError> findAccessibleCareCircle(UUID careCircleId, UUID requesterId) {
        var careCircle = careCircleRepository.findById(new CareCircleId(careCircleId));
        if (careCircle.isEmpty())
            return Result.failure(ApplicationError.notFound("care_circle", careCircleId.toString()));
        if (!careCircle.get().hasAccess(requesterId))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));
        return Result.success(careCircle.get());
    }
}
