package com.serenia.platform.iam.application.acl;

import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.serenia.platform.iam.domain.services.UserQueryService;
import com.serenia.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation of {@link IamContextFacade}.
 *
 * <p>Resolves the queries of other bounded contexts from the requested account and
 * returns them as primitive types.</p>
 */
@Service
public class IamContextFacadeImpl implements IamContextFacade {

    private final UserQueryService userQueryService;

    public IamContextFacadeImpl(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @Override
    public boolean existsActiveUserById(UUID userId) {
        return findUser(userId).map(User::isActive).orElse(false);
    }

    @Override
    public Optional<String> fetchUserRoleById(UUID userId) {
        return findUser(userId).map(user -> user.getRole().name());
    }

    @Override
    public Optional<String> fetchFullNameById(UUID userId) {
        return findUser(userId).map(user -> user.getFullName().value());
    }

    @Override
    public Optional<String> fetchTimeZoneById(UUID userId) {
        return findUser(userId).map(user -> user.getTimeZone().getId());
    }

    private Optional<User> findUser(UUID userId) {
        if (userId == null) return Optional.empty();
        return userQueryService.handle(new GetUserByIdQuery(userId));
    }
}
