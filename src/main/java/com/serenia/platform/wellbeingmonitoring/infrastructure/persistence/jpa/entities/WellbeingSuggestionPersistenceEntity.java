package com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.entities;

import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SuggestionStatus;
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
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code wellbeing_suggestions} table.
 *
 * <p>The {@code (older_adult_id, status)} index resolves the search of the active suggestions.</p>
 */
@Entity
@Table(name = "wellbeing_suggestions",
        indexes = @Index(name = "idx_wellbeing_suggestions_older_adult_status", columnList = "older_adult_id, status"))
@Getter
@Setter
@NoArgsConstructor
public class WellbeingSuggestionPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "older_adult_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID olderAdultId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "pattern_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID patternId;

    @Column(name = "message", length = 300, nullable = false, updatable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private SuggestionStatus status;

    @Column(name = "issued_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant issuedAt;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "dismissed_by", columnDefinition = "BINARY(16)")
    private UUID dismissedBy;

    @Column(name = "dismissed_at", columnDefinition = "DATETIME(6)")
    private Instant dismissedAt;
}
