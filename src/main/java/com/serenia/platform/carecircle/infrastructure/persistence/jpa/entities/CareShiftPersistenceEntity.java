package com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code care_shifts} table.
 *
 * <p>The unique {@code (care_circle_id, shift_date)} index guarantees one shift per date and
 * resolves the calendar queries by date range.</p>
 */
@Entity
@Table(name = "care_shifts",
        uniqueConstraints = @UniqueConstraint(name = "uk_care_shifts_care_circle_shift_date",
                columnNames = {"care_circle_id", "shift_date"}))
@Getter
@Setter
@NoArgsConstructor
public class CareShiftPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "care_circle_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID careCircleId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "relative_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID relativeId;

    @Column(name = "shift_date", nullable = false, updatable = false)
    private LocalDate shiftDate;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant updatedAt;
}
