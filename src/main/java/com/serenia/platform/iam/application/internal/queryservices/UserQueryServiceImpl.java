package com.serenia.platform.iam.application.internal.queryservices;

import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.iam.domain.repositories.UserRepository;
import com.serenia.platform.iam.domain.services.UserQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service that resolves the read operations on the {@link User} aggregate
 * without modifying its state.
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(new UserId(query.userId()));
    }
}
