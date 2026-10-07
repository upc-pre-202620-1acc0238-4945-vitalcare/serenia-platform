package com.serenia.platform.iam.domain.repositories;

import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.valueobjects.EmailAddress;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;

import java.util.Optional;

/**
 * Persistence contract of the {@link User} aggregate.
 *
 * <p>Free of any JPA or Spring Data dependency; the infrastructure layer provides
 * the implementation.</p>
 */
public interface UserRepository {

    /**
     * Persists the account.
     *
     * @throws com.serenia.platform.iam.domain.exceptions.EmailAlreadyRegisteredException
     *         when another account already uses the same email
     */
    User save(User user);

    Optional<User> findById(UserId userId);

    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);
}
