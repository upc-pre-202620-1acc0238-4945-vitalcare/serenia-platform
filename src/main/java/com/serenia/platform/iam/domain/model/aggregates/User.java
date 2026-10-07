package com.serenia.platform.iam.domain.model.aggregates;

import com.serenia.platform.iam.domain.exceptions.InactiveUserAccountException;
import com.serenia.platform.iam.domain.model.events.DistantRelativeRegistered;
import com.serenia.platform.iam.domain.model.events.OlderAdultRegistered;
import com.serenia.platform.iam.domain.model.events.PasswordChanged;
import com.serenia.platform.iam.domain.model.events.ProfileDataUpdated;
import com.serenia.platform.iam.domain.model.events.UserPhotoUpdated;
import com.serenia.platform.iam.domain.model.valueobjects.AccountStatus;
import com.serenia.platform.iam.domain.model.valueobjects.EmailAddress;
import com.serenia.platform.iam.domain.model.valueobjects.LocaleCode;
import com.serenia.platform.iam.domain.model.valueobjects.PasswordHash;
import com.serenia.platform.iam.domain.model.valueobjects.PersonName;
import com.serenia.platform.iam.domain.model.valueobjects.PhoneNumber;
import com.serenia.platform.iam.domain.model.valueobjects.SessionId;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.iam.domain.model.valueobjects.UserRole;
import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Aggregate root representing the account of a person in Serenia, either an
 * older adult or a distant relative.
 *
 * <p>Guards the validity of the credentials, the profile data and the account status.
 * The role is assigned at registration and never changes. Domain events are registered
 * here and published by the application layer after the aggregate is persisted.</p>
 */
@Getter
public class User extends AbstractDomainAggregateRoot<User> {

    private static final String ID_NULL_MESSAGE_KEY = "user.id.null";
    private static final String EMAIL_NULL_MESSAGE_KEY = "user.email.blank";
    private static final String PASSWORD_HASH_NULL_MESSAGE_KEY = "user.password.hash.blank";
    private static final String ROLE_NULL_MESSAGE_KEY = "user.role.null";
    private static final String FULL_NAME_NULL_MESSAGE_KEY = "user.full.name.blank";
    private static final String LOCALE_NULL_MESSAGE_KEY = "user.locale.blank";
    private static final String TIME_ZONE_NULL_MESSAGE_KEY = "user.time.zone.null";
    private static final String STATUS_NULL_MESSAGE_KEY = "user.status.null";
    private static final String BIRTH_DATE_FUTURE_MESSAGE_KEY = "user.birth.date.future";
    private static final String TIMESTAMPS_NULL_MESSAGE_KEY = "user.timestamps.null";

    private final UserId id;
    private final EmailAddress email;
    private PasswordHash passwordHash;
    private final UserRole role;
    private PersonName fullName;
    private PhoneNumber phoneNumber;
    private LocalDate birthDate;
    private String photoUrl;
    private LocaleCode locale;
    private ZoneId timeZone;
    private AccountStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    /**
     * Reconstitution constructor — used by the persistence mapper to rebuild an account
     * from stored data. It validates the invariants but registers no domain events.
     */
    public User(UserId id, EmailAddress email, PasswordHash passwordHash, UserRole role,
                PersonName fullName, PhoneNumber phoneNumber, LocalDate birthDate, String photoUrl,
                LocaleCode locale, ZoneId timeZone, AccountStatus status,
                Instant createdAt, Instant updatedAt) {
        this.id = requireNonNull(id, ID_NULL_MESSAGE_KEY);
        this.email = requireNonNull(email, EMAIL_NULL_MESSAGE_KEY);
        this.passwordHash = requireNonNull(passwordHash, PASSWORD_HASH_NULL_MESSAGE_KEY);
        this.role = requireNonNull(role, ROLE_NULL_MESSAGE_KEY);
        this.fullName = requireNonNull(fullName, FULL_NAME_NULL_MESSAGE_KEY);
        this.phoneNumber = phoneNumber;
        this.locale = requireNonNull(locale, LOCALE_NULL_MESSAGE_KEY);
        this.timeZone = requireNonNull(timeZone, TIME_ZONE_NULL_MESSAGE_KEY);
        this.birthDate = validateBirthDate(birthDate, this.timeZone);
        this.photoUrl = photoUrl;
        this.status = requireNonNull(status, STATUS_NULL_MESSAGE_KEY);
        this.createdAt = requireNonNull(createdAt, TIMESTAMPS_NULL_MESSAGE_KEY);
        this.updatedAt = requireNonNull(updatedAt, TIMESTAMPS_NULL_MESSAGE_KEY);
    }

    /**
     * Creates an active account with the older adult role and registers
     * {@link OlderAdultRegistered}.
     */
    public static User registerOlderAdult(EmailAddress email, PasswordHash passwordHash, PersonName fullName,
                                          PhoneNumber phoneNumber, LocalDate birthDate,
                                          LocaleCode locale, ZoneId timeZone) {
        var user = register(UserRole.OLDER_ADULT, email, passwordHash, fullName, phoneNumber, birthDate, locale, timeZone);
        user.registerDomainEvent(new OlderAdultRegistered(user.id.value(), user.createdAt));
        return user;
    }

    /**
     * Creates an active account with the distant relative role and registers
     * {@link DistantRelativeRegistered}.
     */
    public static User registerDistantRelative(EmailAddress email, PasswordHash passwordHash, PersonName fullName,
                                               PhoneNumber phoneNumber, LocalDate birthDate,
                                               LocaleCode locale, ZoneId timeZone) {
        var user = register(UserRole.DISTANT_RELATIVE, email, passwordHash, fullName, phoneNumber, birthDate, locale, timeZone);
        user.registerDomainEvent(new DistantRelativeRegistered(user.id.value(), user.createdAt));
        return user;
    }

    /** Replaces the personal data of the account and registers {@link ProfileDataUpdated}. */
    public void updateProfileData(PersonName fullName, PhoneNumber phoneNumber, LocalDate birthDate,
                                  LocaleCode locale, ZoneId timeZone) {
        ensureActive();
        var newTimeZone = requireNonNull(timeZone, TIME_ZONE_NULL_MESSAGE_KEY);
        this.birthDate = validateBirthDate(birthDate, newTimeZone);
        this.fullName = requireNonNull(fullName, FULL_NAME_NULL_MESSAGE_KEY);
        this.phoneNumber = phoneNumber;
        this.locale = requireNonNull(locale, LOCALE_NULL_MESSAGE_KEY);
        this.timeZone = newTimeZone;
        this.updatedAt = Instant.now();
        registerDomainEvent(new ProfileDataUpdated(id.value(), updatedAt));
    }

    /** Replaces the profile photo and registers {@link UserPhotoUpdated}. */
    public void updatePhoto(String photoUrl) {
        ensureActive();
        this.photoUrl = photoUrl;
        this.updatedAt = Instant.now();
        registerDomainEvent(new UserPhotoUpdated(id.value(), updatedAt));
    }

    /**
     * Replaces the password hash and registers {@link PasswordChanged}, indicating the
     * session from which the change was made. The caller verifies the current password
     * and hashes the new one before invoking this method.
     */
    public void changePassword(PasswordHash newPasswordHash, SessionId preservedSessionId) {
        ensureActive();
        this.passwordHash = requireNonNull(newPasswordHash, PASSWORD_HASH_NULL_MESSAGE_KEY);
        this.updatedAt = Instant.now();
        registerDomainEvent(new PasswordChanged(
                id.value(),
                preservedSessionId == null ? null : preservedSessionId.value(),
                updatedAt));
    }

    /** Indicates whether the account is in the active status. */
    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    /** Indicates whether the account has the older adult role. */
    public boolean isOlderAdult() {
        return role == UserRole.OLDER_ADULT;
    }

    private static User register(UserRole role, EmailAddress email, PasswordHash passwordHash, PersonName fullName,
                                 PhoneNumber phoneNumber, LocalDate birthDate, LocaleCode locale, ZoneId timeZone) {
        var now = Instant.now();
        return new User(UserId.generate(), email, passwordHash, role, fullName, phoneNumber, birthDate,
                null, locale, timeZone, AccountStatus.ACTIVE, now, now);
    }

    /** Prevents changes on a suspended or deleted account. */
    private void ensureActive() {
        if (!isActive()) {
            throw new InactiveUserAccountException(id.value());
        }
    }

    private static LocalDate validateBirthDate(LocalDate birthDate, ZoneId timeZone) {
        if (birthDate != null && birthDate.isAfter(LocalDate.now(timeZone))) {
            throw new IllegalArgumentException(BIRTH_DATE_FUTURE_MESSAGE_KEY);
        }
        return birthDate;
    }

    private static <T> T requireNonNull(T value, String messageKey) {
        if (value == null) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }
}
