package com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * JPA persistence entity for a row of the {@code small_wins} table.
 *
 * <p>The unique index on {@code check_in_id} prevents duplicating the small win of a check-in
 * evaluated twice; the {@code (older_adult_id, recorded_at)} index resolves the queries by period.</p>
 */
@Entity
@Table(name = "small_wins",
        uniqueConstraints = @UniqueConstraint(name = "uk_small_wins_check_in_id", columnNames = "check_in_id"),
        indexes = @Index(name = "idx_small_wins_older_adult_recorded_at", columnList = "older_adult_id, recorded_at"))
@Getter
@Setter
@NoArgsConstructor
public class SmallWinPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "older_adult_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID olderAdultId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "check_in_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID checkInId;

    @Column(name = "description", length = 200, nullable = false, updatable = false)
    private String description;

    @Column(name = "recorded_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant recordedAt;
}
