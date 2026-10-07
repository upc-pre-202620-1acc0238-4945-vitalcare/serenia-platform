package com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code shared_notes} table.
 *
 * <p>The {@code (care_circle_id, created_at)} index resolves the listing of a circle's
 * notes ordered by date.</p>
 */
@Entity
@Table(name = "shared_notes",
        indexes = @Index(name = "idx_shared_notes_care_circle_created_at", columnList = "care_circle_id, created_at"))
@Getter
@Setter
@NoArgsConstructor
public class SharedNotePersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "care_circle_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID careCircleId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "author_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID authorId;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant updatedAt;
}
