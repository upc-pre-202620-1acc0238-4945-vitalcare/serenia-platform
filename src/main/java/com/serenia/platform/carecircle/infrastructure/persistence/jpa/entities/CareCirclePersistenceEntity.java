package com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code care_circles} table.
 *
 * <p>Owns the collections of invitation codes and family links, persisted in cascade with
 * the circle. Both are loaded eagerly with a sub-select each, because the aggregate is
 * always handled as a whole and the collections are small.</p>
 */
@Entity
@Table(name = "care_circles",
        uniqueConstraints = @UniqueConstraint(name = "uk_care_circles_older_adult_id", columnNames = "older_adult_id"))
@Getter
@Setter
@NoArgsConstructor
public class CareCirclePersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "older_adult_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID olderAdultId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "care_circle_id", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("createdAt ASC")
    private List<InvitationCodePersistenceEntity> invitationCodes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "care_circle_id", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("linkedAt ASC")
    private List<FamilyLinkPersistenceEntity> familyLinks = new ArrayList<>();

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant createdAt;
}
