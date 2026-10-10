package com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.entities;

import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.PatternType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code wellbeing_patterns} table.
 *
 * <p>The {@code (older_adult_id, detected_at)} index resolves the search of the latest pattern.</p>
 */
@Entity
@Table(name = "wellbeing_patterns",
        indexes = @Index(name = "idx_wellbeing_patterns_older_adult_detected_at", columnList = "older_adult_id, detected_at"))
@Getter
@Setter
@NoArgsConstructor
public class WellbeingPatternPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "older_adult_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID olderAdultId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false, updatable = false)
    private PatternType type;

    @Column(name = "consecutive_days", nullable = false)
    private int consecutiveDays;

    @Column(name = "start_date", nullable = false, updatable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "detected_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant detectedAt;
}
