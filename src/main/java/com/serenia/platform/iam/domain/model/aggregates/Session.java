package com.serenia.platform.iam.domain.model.aggregates;

import com.serenia.platform.iam.domain.model.events.UserLoggedIn;
import com.serenia.platform.iam.domain.model.events.UserSessionClosed;
import com.serenia.platform.iam.domain.model.valueobjects.DeviceInfo;
import com.serenia.platform.iam.domain.model.valueobjects.SessionId;
import com.serenia.platform.iam.domain.model.valueobjects.TokenHash;
import com.serenia.platform.iam.domain.model.valueobjects.UserId;
import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/**
 * Aggregate root representing an authenticated access of a user from a device.
 *
 * <p>Modeled apart from {@link User} because sessions grow with every sign-in; the
 * owner is referenced by identity only. The identifier is generated before the token,
 * since the token carries it inside its claims.</p>
 */
@Getter
public class Session extends AbstractDomainAggregateRoot<Session> {

    private static final String ID_NULL_MESSAGE_KEY = "session.id.null";
    private static final String USER_ID_NULL_MESSAGE_KEY = "user.id.null";
    private static final String TOKEN_HASH_NULL_MESSAGE_KEY = "session.token.hash.invalid";
    private static final String TIMESTAMPS_NULL_MESSAGE_KEY = "session.timestamps.null";
    private static final String EXPIRATION_INVALID_MESSAGE_KEY = "session.expiration.invalid";

    private final SessionId id;
    private final UserId userId;
    private final TokenHash tokenHash;
    private final DeviceInfo deviceInfo;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private Instant revokedAt;

    /**
     * Reconstitution constructor — used by the persistence mapper to rebuild a session
     * from stored data. It validates the invariants but registers no domain events.
     */
    public Session(SessionId id, UserId userId, TokenHash tokenHash, DeviceInfo deviceInfo,
                   Instant issuedAt, Instant expiresAt, Instant revokedAt) {
        if (id == null) throw new IllegalArgumentException(ID_NULL_MESSAGE_KEY);
        if (userId == null) throw new IllegalArgumentException(USER_ID_NULL_MESSAGE_KEY);
        if (tokenHash == null) throw new IllegalArgumentException(TOKEN_HASH_NULL_MESSAGE_KEY);
        if (issuedAt == null || expiresAt == null) throw new IllegalArgumentException(TIMESTAMPS_NULL_MESSAGE_KEY);
        if (!expiresAt.isAfter(issuedAt)) throw new IllegalArgumentException(EXPIRATION_INVALID_MESSAGE_KEY);
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.deviceInfo = deviceInfo;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
    }

    /**
     * Opens a session, validating that the expiration is after the issue instant,
     * and registers {@link UserLoggedIn}.
     */
    public static Session open(SessionId sessionId, UserId userId, TokenHash tokenHash, DeviceInfo deviceInfo,
                               Instant issuedAt, Instant expiresAt) {
        var session = new Session(sessionId, userId, tokenHash, deviceInfo, issuedAt, expiresAt, null);
        session.registerDomainEvent(new UserLoggedIn(userId.value(), sessionId.value(), issuedAt));
        return session;
    }

    /**
     * Closes the session by the user's decision and registers {@link UserSessionClosed}.
     * Does nothing if the session was already closed or revoked.
     */
    public void close(Instant closedAt) {
        if (isClosed()) return;
        this.revokedAt = closedAt;
        registerDomainEvent(new UserSessionClosed(userId.value(), id.value(), closedAt));
    }

    /**
     * Marks the session as revoked by a security action.
     * Does nothing if the session was already closed or revoked.
     */
    public void revoke(Instant revokedAt) {
        if (isClosed()) return;
        this.revokedAt = revokedAt;
    }

    /** Indicates whether the session belongs to the given user. */
    public boolean belongsTo(UserId userId) {
        return this.userId.equals(userId);
    }

    /** Compares the hash of the presented token with the stored one. */
    public boolean matchesToken(TokenHash tokenHash) {
        return this.tokenHash.equals(tokenHash);
    }

    /** Indicates whether the token has reached its expiration instant. */
    public boolean isExpired(Instant referenceTime) {
        return !referenceTime.isBefore(expiresAt);
    }

    /** Indicates whether the session is neither closed, revoked nor expired. */
    public boolean isActive(Instant referenceTime) {
        return !isClosed() && !isExpired(referenceTime);
    }

    private boolean isClosed() {
        return revokedAt != null;
    }
}
