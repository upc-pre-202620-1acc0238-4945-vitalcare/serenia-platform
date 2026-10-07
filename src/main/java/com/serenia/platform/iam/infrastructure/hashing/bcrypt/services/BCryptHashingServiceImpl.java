package com.serenia.platform.iam.infrastructure.hashing.bcrypt.services;

import com.serenia.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt implementation of {@link HashingService}.
 */
@Service
public class BCryptHashingServiceImpl implements HashingService {

    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptHashingServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String passwordHash) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}
