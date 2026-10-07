package com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities;

import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code invitation_codes} table.
 *
 * <p>Contained in {@link CareCirclePersistenceEntity}. The {@code (status, expires_at)} index
 * resolves the search of due codes run by the expiration job.</p>
 */
@Entity
@Table(name = "invitation_codes",
        uniqueConstraints = @UniqueConstraint(name = "uk_invitation_codes_code", columnNames = "code"),
        indexes = @Index(name = "idx_invitation_codes_status_expires_at", columnList = "status, expires_at"))
@Getter
@Setter
@NoArgsConstructor
public class InvitationCodePersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", columnDefinition = "CHAR(8)", nullable = false, updatable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private InvitationStatus status;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "used_at", columnDefinition = "DATETIME(6)")
    private Instant usedAt;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "used_by", columnDefinition = "BINARY(16)")
    private UUID usedBy;
}
