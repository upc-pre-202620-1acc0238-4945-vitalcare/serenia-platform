package com.serenia.platform.carecircle.application.internal.errors;

import com.serenia.platform.carecircle.domain.exceptions.CareCircleAccessDeniedException;
import com.serenia.platform.carecircle.domain.exceptions.CareCircleDomainException;
import com.serenia.platform.carecircle.domain.exceptions.CareShiftAlreadyAssignedToRelativeException;
import com.serenia.platform.carecircle.domain.exceptions.CareShiftDateAlreadyCoveredException;
import com.serenia.platform.carecircle.domain.exceptions.CareShiftDateInPastException;
import com.serenia.platform.carecircle.domain.exceptions.FamilyLinkAlreadyRevokedException;
import com.serenia.platform.carecircle.domain.exceptions.FamilyLinkNotFoundException;
import com.serenia.platform.carecircle.domain.exceptions.InvitationCodeNotFoundException;
import com.serenia.platform.carecircle.domain.exceptions.InvitationCodeNotRedeemableException;
import com.serenia.platform.carecircle.domain.exceptions.RelativeAlreadyLinkedException;
import com.serenia.platform.carecircle.domain.exceptions.SharedNoteNotAuthoredException;
import com.serenia.platform.shared.application.result.ApplicationError;

/**
 * Translates the business-rule violations of the Care Circle model into application errors,
 * so every command service maps them to the same HTTP status.
 */
public final class CareCircleErrorMapper {

    private CareCircleErrorMapper() {
    }

    public static ApplicationError toApplicationError(CareCircleDomainException exception) {
        var messageKey = exception.getMessage();
        return switch (exception) {
            case CareCircleAccessDeniedException _, SharedNoteNotAuthoredException _ ->
                    ApplicationError.forbidden(messageKey);
            case InvitationCodeNotFoundException _ -> ApplicationError.notFound("invitation_code", messageKey);
            case FamilyLinkNotFoundException _ -> ApplicationError.notFound("family_link", messageKey);
            case InvitationCodeNotRedeemableException _ -> ApplicationError.conflict("invitation_code", messageKey);
            case RelativeAlreadyLinkedException _, FamilyLinkAlreadyRevokedException _ ->
                    ApplicationError.conflict("family_link", messageKey);
            case CareShiftDateAlreadyCoveredException _ -> ApplicationError.conflict("care_shift", messageKey);
            case CareShiftDateInPastException _, CareShiftAlreadyAssignedToRelativeException _ ->
                    ApplicationError.businessRuleViolation("care_shift", messageKey);
            default -> ApplicationError.businessRuleViolation("care_circle", messageKey);
        };
    }
}
