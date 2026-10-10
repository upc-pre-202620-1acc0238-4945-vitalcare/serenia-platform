package com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.entities;

import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AttentionAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
 * JPA persistence entity for a row of the {@code alert_attentions} table.
 *
 * <p>Contained in {@link AlertPersistenceEntity}. The unique {@code (alert_id, action)} index
 * admits a single acknowledgement and a single resolution per alert, even when two relatives act
 * at the same time.</p>
 */
@Entity
@Table(name = "alert_attentions",
        uniqueConstraints = @UniqueConstraint(name = "uk_alert_attentions_alert_action", columnNames = {"alert_id", "action"}))
@Getter
@Setter
@NoArgsConstructor
public class AlertAttentionPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "relative_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID relativeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", length = 20, nullable = false, updatable = false)
    private AttentionAction action;

    @Column(name = "acted_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant actedAt;

    @Column(name = "resolution_note", length = 300, updatable = false)
    private String resolutionNote;
}
