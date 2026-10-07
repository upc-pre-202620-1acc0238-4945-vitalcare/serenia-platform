package com.serenia.platform.iam.application.internal.commandservices;

import com.serenia.platform.iam.application.internal.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.serenia.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.serenia.platform.iam.domain.model.aggregates.Session;
import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.commands.RevokeSessionsCommand;
import com.serenia.platform.iam.domain.model.commands.SignInCommand;
import com.serenia.platform.iam.domain.model.commands.SignOutCommand;
import com.serenia.platform.iam.domain.model.events.SessionsRevoked;
import com.serenia.platform.iam.domain.model.valueobjects.DeviceInfo;
import com.serenia.platform.iam.domain.model.valueobjects.EmailAddress;
import com.serenia.platform.iam.domain.model.valueobjects.SessionId;
import com.serenia.platform.iam.domain.model.valueobjects.TokenHash;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.iam.domain.repositories.SessionRepository;
import com.serenia.platform.iam.domain.repositories.UserRepository;
import com.serenia.platform.iam.domain.services.SessionCommandService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Application service that handles sign-in, sign-out and revocation of sessions.
 *
 * <p>On sign-in it generates the {@link SessionId} first, issues the token carrying it,
 * and stores only the token hash in the opened {@link Session}.</p>
 */
@Service
public class SessionCommandServiceImpl implements SessionCommandService {

    private static final String INVALID_CREDENTIALS = "user.credentials.invalid";
    private static final String ACCOUNT_NOT_ACTIVE = "user.account.not.active";
    private static final String SESSION_NOT_OWNED = "session.not.owned";

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final DomainEventPublisher domainEventPublisher;
    private final Duration sessionDuration;

    public SessionCommandServiceImpl(UserRepository userRepository,
                                     SessionRepository sessionRepository,
                                     HashingService hashingService,
                                     TokenService tokenService,
                                     DomainEventPublisher domainEventPublisher,
                                     @Value("${authorization.jwt.expiration.days}") int expirationDays) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.domainEventPublisher = domainEventPublisher;
        this.sessionDuration = Duration.ofDays(expirationDays);
    }

    /**
     * Authenticates the user and opens a session. An unknown email and a wrong password
     * produce the same error so the endpoint does not reveal which emails are registered.
     */
    @Override
    public Result<ImmutableTriple<User, Session, String>, ApplicationError> handle(SignInCommand command) {
        var user = userRepository.findByEmail(new EmailAddress(command.email()))
                .filter(candidate -> hashingService.matches(command.password(), candidate.getPasswordHash().value()));
        if (user.isEmpty())
            return Result.failure(ApplicationError.unauthorized(INVALID_CREDENTIALS));
        if (!user.get().isActive())
            return Result.failure(ApplicationError.businessRuleViolation("user", ACCOUNT_NOT_ACTIVE));

        var sessionId = SessionId.generate();
        var issuedAt = Instant.now();
        var expiresAt = issuedAt.plus(sessionDuration);
        var token = tokenService.generateToken(
                user.get().getId().value(), sessionId.value(), user.get().getRole().name(), expiresAt);

        var session = Session.open(
                sessionId,
                user.get().getId(),
                new TokenHash(tokenService.hash(token)),
                DeviceInfo.fromNullable(command.deviceInfo()),
                issuedAt,
                expiresAt);

        return Result.success(ImmutableTriple.of(user.get(), saveAndPublish(session), token));
    }

    @Override
    public Result<Void, ApplicationError> handle(SignOutCommand command) {
        var session = sessionRepository.findById(new SessionId(command.sessionId()));
        if (session.isEmpty())
            return Result.failure(ApplicationError.notFound("session", command.sessionId().toString()));
        if (!session.get().belongsTo(new UserId(command.userId())))
            return Result.failure(ApplicationError.forbidden(SESSION_NOT_OWNED));

        session.get().close(Instant.now());
        saveAndPublish(session.get());
        return Result.success(null);
    }

    /**
     * Revokes every active session of the user except the preserved one and publishes
     * a single {@link SessionsRevoked} event with the affected sessions.
     */
    @Override
    @Transactional
    public Result<List<SessionId>, ApplicationError> handle(RevokeSessionsCommand command) {
        var userId = new UserId(command.userId());
        var now = Instant.now();
        var sessionsToRevoke = sessionRepository.findActiveByUserId(userId, now).stream()
                .filter(session -> !session.getId().value().equals(command.preservedSessionId()))
                .toList();
        if (sessionsToRevoke.isEmpty()) return Result.success(List.of());

        sessionsToRevoke.forEach(session -> session.revoke(now));
        sessionRepository.saveAll(sessionsToRevoke);

        var revokedSessionIds = sessionsToRevoke.stream().map(Session::getId).toList();
        domainEventPublisher.publish(List.of(new SessionsRevoked(
                userId.value(),
                revokedSessionIds.stream().map(SessionId::value).toList(),
                now)));
        return Result.success(revokedSessionIds);
    }

    private Session saveAndPublish(Session session) {
        var saved = sessionRepository.save(session);
        var events = List.copyOf(session.domainEvents());
        session.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }
}
