package com.serenia.platform.socialcompanionship.application.internal.errors;

import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.socialcompanionship.domain.exceptions.InvalidReminderStateException;
import com.serenia.platform.socialcompanionship.domain.exceptions.MessageAlreadySharedException;
import com.serenia.platform.socialcompanionship.domain.exceptions.MessageWithoutRecipientsException;
import com.serenia.platform.socialcompanionship.domain.exceptions.NoTimeLeftTodayException;
import com.serenia.platform.socialcompanionship.domain.exceptions.NotMessageRecipientException;
import com.serenia.platform.socialcompanionship.domain.exceptions.SocialCompanionshipDomainException;

/**
 * Translates the business-rule violations of the Social Companionship model into application errors.
 */
public final class SocialCompanionshipErrorMapper {

    private SocialCompanionshipErrorMapper() {
    }

    public static ApplicationError toApplicationError(SocialCompanionshipDomainException exception) {
        var messageKey = exception.getMessage();
        return switch (exception) {
            case NotMessageRecipientException _ -> ApplicationError.forbidden(messageKey);
            case MessageAlreadySharedException _ -> ApplicationError.conflict("companion_message", messageKey);
            case MessageWithoutRecipientsException _ -> ApplicationError.businessRuleViolation("companion_message", messageKey);
            case InvalidReminderStateException _, NoTimeLeftTodayException _ ->
                    ApplicationError.conflict("social_reminder", messageKey);
            default -> ApplicationError.businessRuleViolation("social_companionship", messageKey);
        };
    }
}
