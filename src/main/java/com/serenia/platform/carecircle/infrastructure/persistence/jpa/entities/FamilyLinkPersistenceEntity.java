package com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities;

import com.serenia.platform.carecircle.domain.model.valueobjects.LinkStatus;
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
 * JPA persistence entity for a row of the {@code family_links} table.
 *
 * <p>Contained in {@link CareCirclePersistenceEntity}. A relative has at most one link per
 * circle, since a revoked link is reactivated instead of creating another one.</p>
 */
@Entity
@Table(name = "family_links",
        uniqueConstraints = @UniqueConstraint(name = "uk_family_links_care_circle_relative",
                columnNames = {"care_circle_id", "relative_id"}),
        indexes = @Index(name = "idx_family_links_relative_id_status", columnList = "relative_id, status"))
@Getter
@Setter
@NoArgsConstructor
public class FamilyLinkPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "relative_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID relativeId;

    @Column(name = "relationship_label", length = 60)
    private String relationshipLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private LinkStatus status;

    @Column(name = "linked_at", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant linkedAt;

    @Column(name = "revoked_at", columnDefinition = "DATETIME(6)")
    private Instant revokedAt;
}
