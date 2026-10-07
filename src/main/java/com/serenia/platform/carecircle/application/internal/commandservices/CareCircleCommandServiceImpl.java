package com.serenia.platform.carecircle.application.internal.commandservices;

import com.serenia.platform.carecircle.application.internal.errors.CareCircleErrorMapper;
import com.serenia.platform.carecircle.application.internal.outboundservices.acl.ExternalIamService;
import com.serenia.platform.carecircle.application.internal.outboundservices.codes.InvitationCodeGenerator;
import com.serenia.platform.carecircle.domain.exceptions.CareCircleDomainException;
import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.domain.model.commands.CreateCareCircleCommand;
import com.serenia.platform.carecircle.domain.model.commands.EstablishFamilyLinkCommand;
import com.serenia.platform.carecircle.domain.model.commands.ExpireInvitationCodeCommand;
import com.serenia.platform.carecircle.domain.model.commands.GenerateInvitationCodeCommand;
import com.serenia.platform.carecircle.domain.model.commands.RedeemInvitationCodeCommand;
import com.serenia.platform.carecircle.domain.model.commands.RevokeFamilyLinkCommand;
import com.serenia.platform.carecircle.domain.model.entities.FamilyLink;
import com.serenia.platform.carecircle.domain.model.entities.InvitationCode;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.FamilyLinkId;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeId;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeValue;
import com.serenia.platform.carecircle.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelationshipLabel;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.domain.repositories.CareCircleRepository;
import com.serenia.platform.carecircle.domain.services.CareCircleCommandService;
import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service that handles the write operations on care circles, their invitation
 * codes and their family links. Domain events accumulated by the aggregate are published
 * after it is persisted.
 */
@Service
public class CareCircleCommandServiceImpl implements CareCircleCommandService {

    private static final int MAX_CODE_GENERATION_ATTEMPTS = 10;
    private static final String CODE_GENERATION_FAILED = "invitation.code.generation.failed";
    private static final String REDEEMER_NOT_DISTANT_RELATIVE = "care.circle.redeemer.not.distant.relative";
    private static final String INVITATION_CODE_NOT_FOUND = "invitation.code.not.found";

    private final CareCircleRepository careCircleRepository;
    private final InvitationCodeGenerator invitationCodeGenerator;
    private final ExternalIamService externalIamService;
    private final DomainEventPublisher domainEventPublisher;
    private final Duration invitationCodeValidity;

    public CareCircleCommandServiceImpl(CareCircleRepository careCircleRepository,
                                        InvitationCodeGenerator invitationCodeGenerator,
                                        ExternalIamService externalIamService,
                                        DomainEventPublisher domainEventPublisher,
                                        @Value("${care-circle.invitation-code.validity}") Duration invitationCodeValidity) {
        this.careCircleRepository = careCircleRepository;
        this.invitationCodeGenerator = invitationCodeGenerator;
        this.externalIamService = externalIamService;
        this.domainEventPublisher = domainEventPublisher;
        this.invitationCodeValidity = invitationCodeValidity;
    }

    /** Creates the circle of the older adult without duplicating it if one already exists. */
    @Override
    public Result<CareCircle, ApplicationError> handle(CreateCareCircleCommand command) {
        var olderAdultId = new OlderAdultId(command.olderAdultId());
        var existing = careCircleRepository.findByOlderAdultId(olderAdultId);
        if (existing.isPresent()) return Result.success(existing.get());
        return Result.success(saveAndPublish(CareCircle.create(olderAdultId, Instant.now())));
    }

    /**
     * Generates a code, asking for new values until one that does not exist yet is
     * obtained, with the configured validity.
     */
    @Override
    public Result<InvitationCode, ApplicationError> handle(GenerateInvitationCodeCommand command) {
        var careCircle = careCircleRepository.findById(new CareCircleId(command.careCircleId()));
        if (careCircle.isEmpty()) return careCircleNotFound(command.careCircleId());

        var code = nextUniqueInvitationCode();
        if (code.isEmpty())
            return Result.failure(ApplicationError.unexpected("care_circle", CODE_GENERATION_FAILED));

        var now = Instant.now();
        try {
            var invitationCode = careCircle.get().generateInvitationCode(
                    command.requesterId(), code.get(), now, now.plus(invitationCodeValidity));
            saveAndPublish(careCircle.get());
            return Result.success(invitationCode);
        } catch (CareCircleDomainException e) {
            return Result.failure(CareCircleErrorMapper.toApplicationError(e));
        }
    }

    @Override
    public Result<Void, ApplicationError> handle(ExpireInvitationCodeCommand command) {
        var careCircle = careCircleRepository.findById(new CareCircleId(command.careCircleId()));
        if (careCircle.isEmpty()) return careCircleNotFound(command.careCircleId());
        try {
            var expired = careCircle.get().expireInvitationCode(
                    new InvitationCodeId(command.invitationCodeId()), Instant.now());
            if (expired) saveAndPublish(careCircle.get());
            return Result.success(null);
        } catch (CareCircleDomainException e) {
            return Result.failure(CareCircleErrorMapper.toApplicationError(e));
        }
    }

    /**
     * Redeems the code after checking that the requester is an active distant relative.
     * Runs in a single transaction so the consumption of the code and the creation of the
     * link, made by the {@code InvitationCodeRedeemed} handler, happen together.
     */
    @Override
    @Transactional
    public Result<CareCircle, ApplicationError> handle(RedeemInvitationCodeCommand command) {
        if (!externalIamService.isActiveDistantRelative(command.relativeId()))
            return Result.failure(ApplicationError.forbidden(REDEEMER_NOT_DISTANT_RELATIVE));

        var code = toInvitationCodeValue(command.code());
        var careCircle = code.flatMap(careCircleRepository::findByInvitationCode);
        if (careCircle.isEmpty())
            return Result.failure(ApplicationError.notFound("invitation_code", INVITATION_CODE_NOT_FOUND));

        try {
            careCircle.get().redeemInvitationCode(
                    code.get(),
                    new RelativeId(command.relativeId()),
                    RelationshipLabel.fromNullable(command.relationshipLabel()),
                    Instant.now());
        } catch (CareCircleDomainException e) {
            return Result.failure(CareCircleErrorMapper.toApplicationError(e));
        }
        saveAndPublish(careCircle.get());

        // Reload the circle to include the link established by the event handler
        return careCircleRepository.findById(careCircle.get().getId())
                .<Result<CareCircle, ApplicationError>>map(Result::success)
                .orElseGet(() -> careCircleNotFound(careCircle.get().getId().value()));
    }

    @Override
    public Result<FamilyLink, ApplicationError> handle(EstablishFamilyLinkCommand command) {
        var careCircle = careCircleRepository.findById(new CareCircleId(command.careCircleId()));
        if (careCircle.isEmpty()) return careCircleNotFound(command.careCircleId());
        try {
            var familyLink = careCircle.get().establishFamilyLink(
                    new RelativeId(command.relativeId()),
                    RelationshipLabel.fromNullable(command.relationshipLabel()),
                    Instant.now());
            saveAndPublish(careCircle.get());
            return Result.success(familyLink);
        } catch (CareCircleDomainException e) {
            return Result.failure(CareCircleErrorMapper.toApplicationError(e));
        }
    }

    @Override
    public Result<Void, ApplicationError> handle(RevokeFamilyLinkCommand command) {
        var careCircle = careCircleRepository.findById(new CareCircleId(command.careCircleId()));
        if (careCircle.isEmpty()) return careCircleNotFound(command.careCircleId());
        try {
            careCircle.get().revokeFamilyLink(
                    new FamilyLinkId(command.familyLinkId()), command.requesterId(), Instant.now());
            saveAndPublish(careCircle.get());
            return Result.success(null);
        } catch (CareCircleDomainException e) {
            return Result.failure(CareCircleErrorMapper.toApplicationError(e));
        }
    }

    private Optional<InvitationCodeValue> nextUniqueInvitationCode() {
        for (int attempt = 0; attempt < MAX_CODE_GENERATION_ATTEMPTS; attempt++) {
            var code = invitationCodeGenerator.generate();
            if (careCircleRepository.findByInvitationCode(code).isEmpty()) return Optional.of(code);
        }
        return Optional.empty();
    }

    /** A malformed code cannot exist, so it is reported as not found. */
    private static Optional<InvitationCodeValue> toInvitationCodeValue(String code) {
        try {
            return Optional.of(new InvitationCodeValue(code));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private CareCircle saveAndPublish(CareCircle careCircle) {
        var saved = careCircleRepository.save(careCircle);
        var events = List.copyOf(careCircle.domainEvents());
        careCircle.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }

    private static <T> Result<T, ApplicationError> careCircleNotFound(UUID careCircleId) {
        return Result.failure(ApplicationError.notFound("care_circle", careCircleId.toString()));
    }
}
