package com.serenia.platform.carecircle.domain.services;

import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.domain.model.commands.CreateCareCircleCommand;
import com.serenia.platform.carecircle.domain.model.commands.EstablishFamilyLinkCommand;
import com.serenia.platform.carecircle.domain.model.commands.ExpireInvitationCodeCommand;
import com.serenia.platform.carecircle.domain.model.commands.GenerateInvitationCodeCommand;
import com.serenia.platform.carecircle.domain.model.commands.RedeemInvitationCodeCommand;
import com.serenia.platform.carecircle.domain.model.commands.RevokeFamilyLinkCommand;
import com.serenia.platform.carecircle.domain.model.entities.FamilyLink;
import com.serenia.platform.carecircle.domain.model.entities.InvitationCode;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;

/**
 * Contract of the write operations on care circles, their invitation codes and their family links.
 */
public interface CareCircleCommandService {

    /** Creates the circle of an older adult, or returns the existing one. */
    Result<CareCircle, ApplicationError> handle(CreateCareCircleCommand command);

    Result<InvitationCode, ApplicationError> handle(GenerateInvitationCodeCommand command);

    Result<Void, ApplicationError> handle(ExpireInvitationCodeCommand command);

    /**
     * Redeems a code and links the relative to the circle.
     *
     * @return the circle the relative joined, including the new active link
     */
    Result<CareCircle, ApplicationError> handle(RedeemInvitationCodeCommand command);

    Result<FamilyLink, ApplicationError> handle(EstablishFamilyLinkCommand command);

    Result<Void, ApplicationError> handle(RevokeFamilyLinkCommand command);
}
