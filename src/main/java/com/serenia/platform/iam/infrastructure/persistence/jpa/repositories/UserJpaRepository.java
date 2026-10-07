package com.serenia.platform.iam.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link UserPersistenceEntity}.
 *
 * <p>The email lookups are resolved over the unique index of the {@code email} column.</p>
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserPersistenceEntity, UUID> {

    boolean existsByEmail(String email);

    Optional<UserPersistenceEntity> findByEmail(String email);
}
