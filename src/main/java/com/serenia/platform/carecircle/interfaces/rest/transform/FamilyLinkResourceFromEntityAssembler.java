package com.serenia.platform.carecircle.interfaces.rest.transform;

import com.serenia.platform.carecircle.domain.model.entities.FamilyLink;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.interfaces.rest.resources.FamilyLinkResource;

/**
 * Converts a {@link FamilyLink} entity into its {@link FamilyLinkResource} REST representation.
 *
 * <p>The circle is passed apart because the link, as an internal entity, does not hold it.</p>
 */
public class FamilyLinkResourceFromEntityAssembler {
    public static FamilyLinkResource toResourceFromEntity(CareCircleId careCircleId, FamilyLink familyLink) {
        return new FamilyLinkResource(
                familyLink.getId().value(),
                careCircleId.value(),
                familyLink.getRelativeId().value(),
                familyLink.getRelationshipLabel() == null ? null : familyLink.getRelationshipLabel().value(),
                familyLink.getStatus().name(),
                familyLink.getLinkedAt());
    }
}
