package com.serenia.platform.carecircle.domain.model.aggregates;

import com.serenia.platform.carecircle.domain.exceptions.CareCircleAccessDeniedException;
import com.serenia.platform.carecircle.domain.exceptions.FamilyLinkAlreadyRevokedException;
import com.serenia.platform.carecircle.domain.exceptions.FamilyLinkNotFoundException;
import com.serenia.platform.carecircle.domain.exceptions.InvitationCodeNotFoundException;
import com.serenia.platform.carecircle.domain.exceptions.InvitationCodeNotRedeemableException;
import com.serenia.platform.carecircle.domain.exceptions.RelativeAlreadyLinkedException;
import com.serenia.platform.carecircle.domain.model.entities.FamilyLink;
import com.serenia.platform.carecircle.domain.model.entities.InvitationCode;
import com.serenia.platform.carecircle.domain.model.events.CareCircleCreated;
import com.serenia.platform.carecircle.domain.model.events.FamilyLinkEstablished;
import com.serenia.platform.carecircle.domain.model.events.FamilyLinkRevoked;
import com.serenia.platform.carecircle.domain.model.events.InvitationCodeExpired;
import com.serenia.platform.carecircle.domain.model.events.InvitationCodeGenerated;
import com.serenia.platform.carecircle.domain.model.events.InvitationCodeRedeemed;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.FamilyLinkId;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeId;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeValue;
import com.serenia.platform.carecircle.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelationshipLabel;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Aggregate root representing the support network of an older adult.
 *
 * <p>Groups the invitation codes and the family links because their rules must be checked
 * together: a code is redeemed only once and a relative cannot hold two active links with
 * the same circle. Every older adult has exactly one circle.</p>
 */
public class CareCircle extends AbstractDomainAggregateRoot<CareCircle> {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "care.circle.required.value";
    private static final String ONLY_OWNER_CAN_INVITE_MESSAGE_KEY = "care.circle.only.owner.can.invite";
    private static final String CANNOT_REVOKE_LINK_MESSAGE_KEY = "care.circle.cannot.revoke.family.link";

    private final CareCircleId id;
    private final OlderAdultId olderAdultId;
    private final List<InvitationCode> invitationCodes;
    private final List<FamilyLink> familyLinks;
    private final Instant createdAt;

    /**
     * Reconstitution constructor — used by the persistence mapper to rebuild a circle,
     * with its codes and links, from stored data.
     */
    public CareCircle(CareCircleId id, OlderAdultId olderAdultId, List<InvitationCode> invitationCodes,
                      List<FamilyLink> familyLinks, Instant createdAt) {
        if (id == null || olderAdultId == null || createdAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.olderAdultId = olderAdultId;
        this.invitationCodes = invitationCodes == null ? new ArrayList<>() : new ArrayList<>(invitationCodes);
        this.familyLinks = familyLinks == null ? new ArrayList<>() : new ArrayList<>(familyLinks);
        this.createdAt = createdAt;
    }

    /** Creates the circle of an older adult and registers {@link CareCircleCreated}. */
    public static CareCircle create(OlderAdultId olderAdultId, Instant createdAt) {
        var careCircle = new CareCircle(CareCircleId.generate(), olderAdultId, List.of(), List.of(), createdAt);
        careCircle.registerDomainEvent(new CareCircleCreated(careCircle.id.value(), olderAdultId.value(), createdAt));
        return careCircle;
    }

    /**
     * Generates a pending invitation code; only the owner older adult can request it.
     * Registers {@link InvitationCodeGenerated}.
     */
    public InvitationCode generateInvitationCode(UUID requesterId, InvitationCodeValue code,
                                                 Instant createdAt, Instant expiresAt) {
        if (!isOwner(requesterId))
            throw new CareCircleAccessDeniedException(ONLY_OWNER_CAN_INVITE_MESSAGE_KEY);
        var invitationCode = InvitationCode.issue(code, createdAt, expiresAt);
        invitationCodes.add(invitationCode);
        registerDomainEvent(new InvitationCodeGenerated(
                id.value(), invitationCode.getId().value(), expiresAt, createdAt));
        return invitationCode;
    }

    /**
     * Marks as expired a pending code whose validity has ended and registers
     * {@link InvitationCodeExpired}. Does nothing if the code is not due for expiration.
     *
     * @return {@code true} if the code was expired by this call
     */
    public boolean expireInvitationCode(InvitationCodeId invitationCodeId, Instant referenceTime) {
        var invitationCode = invitationCodes.stream()
                .filter(candidate -> candidate.getId().equals(invitationCodeId))
                .findFirst()
                .orElseThrow(InvitationCodeNotFoundException::new);
        if (!invitationCode.isDueForExpiration(referenceTime)) return false;
        invitationCode.expire();
        registerDomainEvent(new InvitationCodeExpired(id.value(), invitationCodeId.value(), referenceTime));
        return true;
    }

    /**
     * Marks the code as used by the relative and registers {@link InvitationCodeRedeemed}.
     * Rejects the redemption if the code was already used, if its validity ended even
     * though it was not marked as expired yet, or if the relative already has an active link.
     */
    public void redeemInvitationCode(InvitationCodeValue code, RelativeId relativeId,
                                     RelationshipLabel relationshipLabel, Instant redeemedAt) {
        var invitationCode = invitationCodes.stream()
                .filter(candidate -> candidate.getCode().equals(code))
                .findFirst()
                .orElseThrow(InvitationCodeNotFoundException::new);
        if (!invitationCode.isRedeemableAt(redeemedAt)) throw new InvitationCodeNotRedeemableException();
        if (isActiveRelative(relativeId)) throw new RelativeAlreadyLinkedException();

        invitationCode.markAsUsed(relativeId, redeemedAt);
        registerDomainEvent(new InvitationCodeRedeemed(
                id.value(),
                invitationCode.getId().value(),
                relativeId.value(),
                relationshipLabel == null ? null : relationshipLabel.value(),
                redeemedAt));
    }

    /**
     * Creates an active link with the relative; if a previously revoked link exists, it is
     * reactivated instead of creating another one. Registers {@link FamilyLinkEstablished}.
     */
    public FamilyLink establishFamilyLink(RelativeId relativeId, RelationshipLabel relationshipLabel, Instant linkedAt) {
        if (isActiveRelative(relativeId)) throw new RelativeAlreadyLinkedException();
        var familyLink = familyLinks.stream()
                .filter(candidate -> candidate.getRelativeId().equals(relativeId))
                .findFirst()
                .map(revokedLink -> {
                    revokedLink.reactivate(relationshipLabel, linkedAt);
                    return revokedLink;
                })
                .orElseGet(() -> {
                    var newLink = FamilyLink.establish(relativeId, relationshipLabel, linkedAt);
                    familyLinks.add(newLink);
                    return newLink;
                });
        registerDomainEvent(new FamilyLinkEstablished(
                id.value(), familyLink.getId().value(), relativeId.value(), linkedAt));
        return familyLink;
    }

    /**
     * Revokes an active link. The older adult can revoke any link of the circle;
     * a relative, only their own. Registers {@link FamilyLinkRevoked}.
     */
    public void revokeFamilyLink(FamilyLinkId familyLinkId, UUID requesterId, Instant revokedAt) {
        var familyLink = familyLinks.stream()
                .filter(candidate -> candidate.getId().equals(familyLinkId))
                .findFirst()
                .orElseThrow(FamilyLinkNotFoundException::new);
        var isOwnLink = familyLink.getRelativeId().value().equals(requesterId);
        if (!isOwner(requesterId) && !isOwnLink)
            throw new CareCircleAccessDeniedException(CANNOT_REVOKE_LINK_MESSAGE_KEY);
        if (!familyLink.isActive()) throw new FamilyLinkAlreadyRevokedException();

        familyLink.revoke(revokedAt);
        registerDomainEvent(new FamilyLinkRevoked(
                id.value(), familyLinkId.value(), familyLink.getRelativeId().value(), revokedAt));
    }

    /** Indicates whether the user is the owner older adult or a relative with an active link. */
    public boolean hasAccess(UUID userId) {
        return userId != null && (isOwner(userId) || isActiveRelative(new RelativeId(userId)));
    }

    /** Indicates whether the relative has an active link with the circle. */
    public boolean isActiveRelative(RelativeId relativeId) {
        return familyLinks.stream()
                .anyMatch(link -> link.isActive() && link.getRelativeId().equals(relativeId));
    }

    /** Returns the active links of the circle. */
    public List<FamilyLink> activeFamilyLinks() {
        return familyLinks.stream().filter(FamilyLink::isActive).toList();
    }

    /** Returns the active link of the relative, if any. */
    public Optional<FamilyLink> findActiveFamilyLinkOf(RelativeId relativeId) {
        return activeFamilyLinks().stream()
                .filter(link -> link.getRelativeId().equals(relativeId))
                .findFirst();
    }

    /** Returns the codes that can still be redeemed at the given instant. */
    public List<InvitationCode> redeemableInvitationCodes(Instant referenceTime) {
        return invitationCodes.stream().filter(code -> code.isRedeemableAt(referenceTime)).toList();
    }

    /** Returns the pending codes whose validity has already ended at the given instant. */
    public List<InvitationCode> dueInvitationCodes(Instant referenceTime) {
        return invitationCodes.stream().filter(code -> code.isDueForExpiration(referenceTime)).toList();
    }

    private boolean isOwner(UUID userId) {
        return olderAdultId.value().equals(userId);
    }

    public CareCircleId getId() {
        return id;
    }

    public OlderAdultId getOlderAdultId() {
        return olderAdultId;
    }

    public List<InvitationCode> getInvitationCodes() {
        return Collections.unmodifiableList(invitationCodes);
    }

    public List<FamilyLink> getFamilyLinks() {
        return Collections.unmodifiableList(familyLinks);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
