package com.serenia.platform.iam.application.internal.commandservices;

import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.serenia.platform.iam.domain.exceptions.EmailAlreadyRegisteredException;
import com.serenia.platform.iam.domain.model.aggregates.User;
import com.serenia.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.serenia.platform.iam.domain.model.commands.RegisterDistantRelativeCommand;
import com.serenia.platform.iam.domain.model.commands.RegisterOlderAdultCommand;
import com.serenia.platform.iam.domain.model.commands.UpdateProfileDataCommand;
import com.serenia.platform.iam.domain.model.commands.UpdateUserPhotoCommand;
import com.serenia.platform.iam.domain.model.valueobjects.EmailAddress;
import com.serenia.platform.iam.domain.model.valueobjects.LocaleCode;
import com.serenia.platform.iam.domain.model.valueobjects.PasswordHash;
import com.serenia.platform.iam.domain.model.valueobjects.PersonName;
import com.serenia.platform.iam.domain.model.valueobjects.PhoneNumber;
import com.serenia.platform.iam.domain.model.valueobjects.SessionId;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.iam.domain.repositories.UserRepository;
import com.serenia.platform.iam.domain.services.UserCommandService;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Application service that handles the write operations on the {@link User} aggregate.
 *
 * <p>Registers accounts after checking the email is free and hashing the password,
 * updates profile data and photo, and changes the password after verifying the
 * current one. Domain events accumulated by the aggregate are published after it
 * is persisted.</p>
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private static final String EMAIL_ALREADY_REGISTERED = "user.email.already.registered";
    private static final String ACCOUNT_NOT_ACTIVE = "user.account.not.active";
    private static final String INCORRECT_PASSWORD = "user.password.incorrect";
    private static final String INVALID_TIME_ZONE = "user.time.zone.invalid";

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final DomainEventPublisher domainEventPublisher;

    public UserCommandServiceImpl(UserRepository userRepository,
                                  HashingService hashingService,
                                  DomainEventPublisher domainEventPublisher) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Result<User, ApplicationError> handle(RegisterOlderAdultCommand command) {
        return register(command.email(), email -> User.registerOlderAdult(
                email,
                encodePassword(command.password()),
                new PersonName(command.fullName()),
                PhoneNumber.fromNullable(command.phoneNumber()),
                command.birthDate(),
                new LocaleCode(command.locale()),
                toZoneId(command.timeZone())));
    }

    @Override
    public Result<User, ApplicationError> handle(RegisterDistantRelativeCommand command) {
        return register(command.email(), email -> User.registerDistantRelative(
                email,
                encodePassword(command.password()),
                new PersonName(command.fullName()),
                PhoneNumber.fromNullable(command.phoneNumber()),
                command.birthDate(),
                new LocaleCode(command.locale()),
                toZoneId(command.timeZone())));
    }

    @Override
    public Result<User, ApplicationError> handle(UpdateUserPhotoCommand command) {
        var user = userRepository.findById(new UserId(command.userId()));
        if (user.isEmpty()) return userNotFound(command.userId());
        if (!user.get().isActive()) return accountNotActive();

        user.get().updatePhoto(command.photoUrl());
        return Result.success(saveAndPublish(user.get()));
    }

    @Override
    public Result<User, ApplicationError> handle(UpdateProfileDataCommand command) {
        var user = userRepository.findById(new UserId(command.userId()));
        if (user.isEmpty()) return userNotFound(command.userId());
        if (!user.get().isActive()) return accountNotActive();

        user.get().updateProfileData(
                new PersonName(command.fullName()),
                PhoneNumber.fromNullable(command.phoneNumber()),
                command.birthDate(),
                new LocaleCode(command.locale()),
                toZoneId(command.timeZone()));
        return Result.success(saveAndPublish(user.get()));
    }

    /**
     * Runs in a single transaction so the synchronous revocation of the other
     * sessions, triggered by {@code PasswordChanged}, commits together with the change.
     */
    @Override
    @Transactional
    public Result<Void, ApplicationError> handle(ChangePasswordCommand command) {
        var user = userRepository.findById(new UserId(command.userId()));
        if (user.isEmpty()) return userNotFound(command.userId());
        if (!user.get().isActive()) return accountNotActive();

        if (!hashingService.matches(command.currentPassword(), user.get().getPasswordHash().value()))
            return Result.failure(ApplicationError.businessRuleViolation("user", INCORRECT_PASSWORD));

        user.get().changePassword(encodePassword(command.newPassword()), new SessionId(command.sessionId()));
        saveAndPublish(user.get());
        return Result.success(null);
    }

    private Result<User, ApplicationError> register(String rawEmail, Function<EmailAddress, User> factory) {
        var email = new EmailAddress(rawEmail);
        if (userRepository.existsByEmail(email))
            return Result.failure(ApplicationError.conflict("user", EMAIL_ALREADY_REGISTERED));
        try {
            return Result.success(saveAndPublish(factory.apply(email)));
        } catch (EmailAlreadyRegisteredException e) {
            // Another registration with the same email won the race on the unique index
            return Result.failure(ApplicationError.conflict("user", EMAIL_ALREADY_REGISTERED));
        }
    }

    private User saveAndPublish(User user) {
        var saved = userRepository.save(user);
        var events = List.copyOf(user.domainEvents());
        user.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }

    private PasswordHash encodePassword(String rawPassword) {
        return new PasswordHash(hashingService.encode(rawPassword));
    }

    private static ZoneId toZoneId(String timeZone) {
        try {
            return ZoneId.of(timeZone);
        } catch (DateTimeException | NullPointerException e) {
            throw new IllegalArgumentException(INVALID_TIME_ZONE);
        }
    }

    private static <T> Result<T, ApplicationError> userNotFound(UUID userId) {
        return Result.failure(ApplicationError.notFound("user", userId.toString()));
    }

    private static <T> Result<T, ApplicationError> accountNotActive() {
        return Result.failure(ApplicationError.businessRuleViolation("user", ACCOUNT_NOT_ACTIVE));
    }
}
