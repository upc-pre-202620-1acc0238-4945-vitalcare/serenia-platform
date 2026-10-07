package com.serenia.platform.iam.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.iam.domain.exceptions.EmailAlreadyRegisteredException;
import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.valueobjects.EmailAddress;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.iam.domain.repositories.UserRepository;
import com.serenia.platform.iam.infrastructure.persistence.jpa.mappers.UserPersistenceMapper;
import com.serenia.platform.iam.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter that implements the domain {@link UserRepository} with Spring Data JPA.
 *
 * <p>If two simultaneous registrations use the same email, the violation of the unique
 * index is translated into an {@link EmailAlreadyRegisteredException}.</p>
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        try {
            // Flush immediately so a unique index violation surfaces here and not at commit time
            var saved = userJpaRepository.saveAndFlush(UserPersistenceMapper.toPersistenceFromDomain(user));
            return UserPersistenceMapper.toDomainFromPersistence(saved);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyRegisteredException(user.getEmail().value());
        }
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userJpaRepository.findById(userId.value())
                .map(UserPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return userJpaRepository.findByEmail(email.value())
                .map(UserPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return userJpaRepository.existsByEmail(email.value());
    }
}
