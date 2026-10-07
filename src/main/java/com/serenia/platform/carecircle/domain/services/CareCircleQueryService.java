package com.serenia.platform.carecircle.domain.services;

import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.domain.model.entities.FamilyLink;
import com.serenia.platform.carecircle.domain.model.entities.InvitationCode;
import com.serenia.platform.carecircle.domain.model.queries.GetActiveFamilyLinksByCareCircleIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetActiveFamilyLinksByRelativeIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetCareCircleByIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetCareCircleByOlderAdultIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetDueInvitationCodesQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetPendingInvitationCodesByCareCircleIdQuery;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

import java.util.List;
import java.util.Optional;

/**
 * Contract of the read operations on care circles.
 *
 * <p>Queries issued on behalf of a user return a {@link Result}, so a missing circle
 * and a denied access can be told apart.</p>
 */
public interface CareCircleQueryService {

    Result<CareCircle, ApplicationError> handle(GetCareCircleByIdQuery query);

    Optional<CareCircle> handle(GetCareCircleByOlderAdultIdQuery query);

    Result<List<InvitationCode>, ApplicationError> handle(GetPendingInvitationCodesByCareCircleIdQuery query);

    /** Returns the circles holding pending codes whose validity already ended. */
    List<CareCircle> handle(GetDueInvitationCodesQuery query);

    Result<List<FamilyLink>, ApplicationError> handle(GetActiveFamilyLinksByCareCircleIdQuery query);

    /** Returns the circles in which the relative has an active link. */
    List<CareCircle> handle(GetActiveFamilyLinksByRelativeIdQuery query);
}
