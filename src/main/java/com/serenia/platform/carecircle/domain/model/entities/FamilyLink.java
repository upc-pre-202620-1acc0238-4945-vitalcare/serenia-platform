package com.serenia.platform.carecircle.domain.model.entities;

import com.serenia.platform.carecircle.domain.model.valueobjects.FamilyLinkId;
import com.serenia.platform.carecircle.domain.model.valueobjects.LinkStatus;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelationshipLabel;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import lombok.Getter;

import java.time.Instant;

/**
 * Authorization of a relative to accompany the older adult.
 *
 * <p>Belongs to the {@link com.serenia.platform.carecircle.domain.model.aggregates.CareCircle}
 * aggregate and is never handled outside of it.</p>
 */
@Getter
public class FamilyLink {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "family.link.required.value";

    private final FamilyLinkId id;
    private final RelativeId relativeId;
    private RelationshipLabel relationshipLabel;
    private LinkStatus status;
    private Instant linkedAt;
    private Instant revokedAt;

    /**
     * Reconstitution constructor — used by the persistence mapper to rebuild a link
     * from stored data.
     */
    public FamilyLink(FamilyLinkId id, RelativeId relativeId, RelationshipLabel relationshipLabel,
                      LinkStatus status, Instant linkedAt, Instant revokedAt) {
        if (id == null || relativeId == null || status == null || linkedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.relativeId = relativeId;
        this.relationshipLabel = relationshipLabel;
        this.status = status;
        this.linkedAt = linkedAt;
        this.revokedAt = revokedAt;
    }

    /** Creates a new active link. */
    public static FamilyLink establish(RelativeId relativeId, RelationshipLabel relationshipLabel, Instant linkedAt) {
        return new FamilyLink(FamilyLinkId.generate(), relativeId, relationshipLabel, LinkStatus.ACTIVE, linkedAt, null);
    }

    /** Marks the link as revoked. */
    public void revoke(Instant revokedAt) {
        this.status = LinkStatus.REVOKED;
        this.revokedAt = revokedAt;
    }

    /** Activates a revoked link again, keeping its identity. */
    public void reactivate(RelationshipLabel relationshipLabel, Instant linkedAt) {
        this.status = LinkStatus.ACTIVE;
        this.relationshipLabel = relationshipLabel;
        this.linkedAt = linkedAt;
        this.revokedAt = null;
    }

    /** Indicates whether the link is active. */
    public boolean isActive() {
        return status == LinkStatus.ACTIVE;
    }
}
