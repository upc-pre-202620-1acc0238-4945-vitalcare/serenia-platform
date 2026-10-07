package com.serenia.platform.carecircle.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.domain.model.entities.FamilyLink;
import com.serenia.platform.carecircle.domain.model.entities.InvitationCode;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.FamilyLinkId;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeId;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeValue;
import com.serenia.platform.carecircle.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelationshipLabel;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities.CareCirclePersistenceEntity;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities.FamilyLinkPersistenceEntity;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities.InvitationCodePersistenceEntity;

import java.util.ArrayList;

/**
 * Stateless mapper that translates between the {@link CareCircle} aggregate, with its
 * invitation codes and family links, and its persistence entities.
 */
public final class CareCirclePersistenceMapper {

    private CareCirclePersistenceMapper() {
    }

    /** Reconstructs a {@link CareCircle} aggregate from a stored row and its children. */
    public static CareCircle toDomainFromPersistence(CareCirclePersistenceEntity entity) {
        return new CareCircle(
                new CareCircleId(entity.getId()),
                new OlderAdultId(entity.getOlderAdultId()),
                entity.getInvitationCodes().stream().map(CareCirclePersistenceMapper::toDomainFromPersistence).toList(),
                entity.getFamilyLinks().stream().map(CareCirclePersistenceMapper::toDomainFromPersistence).toList(),
                entity.getCreatedAt());
    }

    /** Converts a {@link CareCircle} aggregate into a row, with its children, ready to be saved. */
    public static CareCirclePersistenceEntity toPersistenceFromDomain(CareCircle careCircle) {
        var entity = new CareCirclePersistenceEntity();
        entity.setId(careCircle.getId().value());
        entity.setOlderAdultId(careCircle.getOlderAdultId().value());
        entity.setCreatedAt(careCircle.getCreatedAt());
        entity.setInvitationCodes(new ArrayList<>(careCircle.getInvitationCodes().stream()
                .map(CareCirclePersistenceMapper::toPersistenceFromDomain)
                .toList()));
        entity.setFamilyLinks(new ArrayList<>(careCircle.getFamilyLinks().stream()
                .map(CareCirclePersistenceMapper::toPersistenceFromDomain)
                .toList()));
        return entity;
    }

    private static InvitationCode toDomainFromPersistence(InvitationCodePersistenceEntity entity) {
        return new InvitationCode(
                new InvitationCodeId(entity.getId()),
                new InvitationCodeValue(entity.getCode()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getUsedAt(),
                entity.getUsedBy() == null ? null : new RelativeId(entity.getUsedBy()));
    }

    private static InvitationCodePersistenceEntity toPersistenceFromDomain(InvitationCode invitationCode) {
        var entity = new InvitationCodePersistenceEntity();
        entity.setId(invitationCode.getId().value());
        entity.setCode(invitationCode.getCode().value());
        entity.setStatus(invitationCode.getStatus());
        entity.setCreatedAt(invitationCode.getCreatedAt());
        entity.setExpiresAt(invitationCode.getExpiresAt());
        entity.setUsedAt(invitationCode.getUsedAt());
        entity.setUsedBy(invitationCode.getUsedBy() == null ? null : invitationCode.getUsedBy().value());
        return entity;
    }

    private static FamilyLink toDomainFromPersistence(FamilyLinkPersistenceEntity entity) {
        return new FamilyLink(
                new FamilyLinkId(entity.getId()),
                new RelativeId(entity.getRelativeId()),
                RelationshipLabel.fromNullable(entity.getRelationshipLabel()),
                entity.getStatus(),
                entity.getLinkedAt(),
                entity.getRevokedAt());
    }

    private static FamilyLinkPersistenceEntity toPersistenceFromDomain(FamilyLink familyLink) {
        var entity = new FamilyLinkPersistenceEntity();
        entity.setId(familyLink.getId().value());
        entity.setRelativeId(familyLink.getRelativeId().value());
        entity.setRelationshipLabel(familyLink.getRelationshipLabel() == null ? null : familyLink.getRelationshipLabel().value());
        entity.setStatus(familyLink.getStatus());
        entity.setLinkedAt(familyLink.getLinkedAt());
        entity.setRevokedAt(familyLink.getRevokedAt());
        return entity;
    }
}
