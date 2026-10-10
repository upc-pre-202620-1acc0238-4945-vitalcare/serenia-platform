package com.serenia.platform.carecircle.domain.model.entities;

import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeId;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeValue;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationStatus;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import lombok.Getter;

import java.time.Instant;

/**
 * Temporary code that lets a relative join a care circle.
 *
 * <p>Belongs to the {@link com.serenia.platform.carecircle.domain.model.aggregates.CareCircle}
 * aggregate and is never handled outside of it.</p>
 */
@Getter
public class InvitationCode {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "invitation.code.required.value";
    private static final String EXPIRATION_INVALID_MESSAGE_KEY = "invitation.code.expiration.invalid";

    private final InvitationCodeId id;
    private final InvitationCodeValue code;
    private InvitationStatus status;
    private final Instant createdAt;
    private final Instant expiresAt;
    private Instant usedAt;
    private RelativeId usedBy;

    /**
     * Reconstitution constructor — used by the persistence mapper to rebuild a code
     * from stored data.
     */
    public InvitationCode(InvitationCodeId id, InvitationCodeValue code, InvitationStatus status,
                          Instant createdAt, Instant expiresAt, Instant usedAt, RelativeId usedBy) {
        if (id == null || code == null || status == null || createdAt == null || expiresAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        if (!expiresAt.isAfter(createdAt))
            throw new IllegalArgumentException(EXPIRATION_INVALID_MESSAGE_KEY);
        this.id = id;
        this.code = code;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.usedBy = usedBy;
    }

    /** Creates a new pending code. */
    public static InvitationCode issue(InvitationCodeValue code, Instant createdAt, Instant expiresAt) {
        return new InvitationCode(InvitationCodeId.generate(), code, InvitationStatus.PENDING,
                createdAt, expiresAt, null, null);
    }

    /** Indicates whether the code is pending and its validity has not ended at the given instant. */
    public boolean isRedeemableAt(Instant referenceTime) {
        return status == InvitationStatus.PENDING && referenceTime.isBefore(expiresAt);
    }

    /** Records the redemption of the code by the relative. */
    public void markAsUsed(RelativeId relativeId, Instant usedAt) {
        this.status = InvitationStatus.USED;
        this.usedBy = relativeId;
        this.usedAt = usedAt;
    }

    /** Indicates whether the code is still pending although its validity has already ended. */
    public boolean isDueForExpiration(Instant referenceTime) {
        return status == InvitationStatus.PENDING && !referenceTime.isBefore(expiresAt);
    }

    /** Marks the code as expired. */
    public void expire() {
        this.status = InvitationStatus.EXPIRED;
    }
}
