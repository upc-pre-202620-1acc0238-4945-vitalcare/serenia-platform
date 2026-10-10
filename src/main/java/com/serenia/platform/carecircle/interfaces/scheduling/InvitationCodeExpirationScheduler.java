package com.serenia.platform.carecircle.interfaces.scheduling;

import com.serenia.platform.carecircle.domain.model.commands.ExpireInvitationCodeCommand;
import com.serenia.platform.carecircle.domain.model.queries.GetDueInvitationCodesQuery;
import com.serenia.platform.carecircle.domain.services.CareCircleCommandService;
import com.serenia.platform.carecircle.domain.services.CareCircleQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Periodic job that looks for the due invitation codes with {@link GetDueInvitationCodesQuery}
 * and sends an {@link ExpireInvitationCodeCommand} for each of them.
 */
@Component
@Slf4j
public class InvitationCodeExpirationScheduler {

    private final CareCircleQueryService careCircleQueryService;
    private final CareCircleCommandService careCircleCommandService;

    public InvitationCodeExpirationScheduler(CareCircleQueryService careCircleQueryService,
                                             CareCircleCommandService careCircleCommandService) {
        this.careCircleQueryService = careCircleQueryService;
        this.careCircleCommandService = careCircleCommandService;
    }

    @Scheduled(fixedDelayString = "${care-circle.invitation-code.expiration-check-interval}")
    public void expireDueInvitationCodes() {
        var now = Instant.now();
        careCircleQueryService.handle(new GetDueInvitationCodesQuery(now)).forEach(careCircle ->
                careCircle.dueInvitationCodes(now).forEach(invitationCode -> {
                    var command = new ExpireInvitationCodeCommand(careCircle.getId().value(), invitationCode.getId().value());
                    if (careCircleCommandService.handle(command).isFailure())
                        log.warn("Could not expire invitation code {} of care circle {}",
                                invitationCode.getId().value(), careCircle.getId().value());
                }));
    }
}
