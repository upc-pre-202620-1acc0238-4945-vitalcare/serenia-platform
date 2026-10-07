package com.serenia.platform.iam.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.valueobjects.EmailAddress;
import com.serenia.platform.iam.domain.model.valueobjects.LocaleCode;
import com.serenia.platform.iam.domain.model.valueobjects.PasswordHash;
import com.serenia.platform.iam.domain.model.valueobjects.PersonName;
import com.serenia.platform.iam.domain.model.valueobjects.PhoneNumber;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

import java.time.ZoneId;

/**
 * Stateless mapper that translates between the {@link User} aggregate and
 * {@link UserPersistenceEntity}, keeping the persistence model out of the domain.
 */
public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    /** Reconstructs a {@link User} aggregate from a stored row. */
    public static User toDomainFromPersistence(UserPersistenceEntity entity) {
        return new User(
                new UserId(entity.getId()),
                new EmailAddress(entity.getEmail()),
                new PasswordHash(entity.getPasswordHash()),
                entity.getRole(),
                new PersonName(entity.getFullName()),
                PhoneNumber.fromNullable(entity.getPhoneNumber()),
                entity.getBirthDate(),
                entity.getPhotoUrl(),
                new LocaleCode(entity.getLocale()),
                ZoneId.of(entity.getTimeZone()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /** Converts a {@link User} aggregate into a row ready to be saved. */
    public static UserPersistenceEntity toPersistenceFromDomain(User user) {
        var entity = new UserPersistenceEntity();
        entity.setId(user.getId().value());
        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPasswordHash().value());
        entity.setRole(user.getRole());
        entity.setFullName(user.getFullName().value());
        entity.setPhoneNumber(user.getPhoneNumber() == null ? null : user.getPhoneNumber().value());
        entity.setBirthDate(user.getBirthDate());
        entity.setPhotoUrl(user.getPhotoUrl());
        entity.setLocale(user.getLocale().value());
        entity.setTimeZone(user.getTimeZone().getId());
        entity.setStatus(user.getStatus());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
}
