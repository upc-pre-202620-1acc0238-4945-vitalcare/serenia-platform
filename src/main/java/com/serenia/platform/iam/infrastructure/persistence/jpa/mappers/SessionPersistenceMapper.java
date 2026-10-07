package com.serenia.platform.iam.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.iam.domain.model.aggregates.Session;
import com.serenia.platform.iam.domain.model.valueobjects.DeviceInfo;
import com.serenia.platform.iam.domain.model.valueobjects.SessionId;
import com.serenia.platform.iam.domain.model.valueobjects.TokenHash;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.iam.infrastructure.persistence.jpa.entities.SessionPersistenceEntity;

/**
 * Stateless mapper that translates between the {@link Session} aggregate and
 * {@link SessionPersistenceEntity}.
 */
public final class SessionPersistenceMapper {

    private SessionPersistenceMapper() {
    }

    /** Reconstructs a {@link Session} aggregate from a stored row. */
    public static Session toDomainFromPersistence(SessionPersistenceEntity entity) {
        return new Session(
                new SessionId(entity.getId()),
                new UserId(entity.getUserId()),
                new TokenHash(entity.getTokenHash()),
                DeviceInfo.fromNullable(entity.getDeviceInfo()),
                entity.getIssuedAt(),
                entity.getExpiresAt(),
                entity.getRevokedAt());
    }

    /** Converts a {@link Session} aggregate into a row ready to be saved. */
    public static SessionPersistenceEntity toPersistenceFromDomain(Session session) {
        var entity = new SessionPersistenceEntity();
        entity.setId(session.getId().value());
        entity.setUserId(session.getUserId().value());
        entity.setTokenHash(session.getTokenHash().value());
        entity.setDeviceInfo(session.getDeviceInfo() == null ? null : session.getDeviceInfo().value());
        entity.setIssuedAt(session.getIssuedAt());
        entity.setExpiresAt(session.getExpiresAt());
        entity.setRevokedAt(session.getRevokedAt());
        return entity;
    }
}
