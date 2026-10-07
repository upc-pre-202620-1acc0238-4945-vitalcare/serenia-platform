package com.serenia.platform.carecircle.infrastructure.codes.services;

import com.serenia.platform.carecircle.application.internal.outboundservices.codes.InvitationCodeGenerator;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeValue;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Generates invitation codes with {@link SecureRandom}, a cryptographically secure
 * generator, so codes cannot be predicted.
 */
@Service
public class SecureRandomInvitationCodeGenerator implements InvitationCodeGenerator {

    private final SecureRandom random = new SecureRandom();

    @Override
    public InvitationCodeValue generate() {
        var alphabet = InvitationCodeValue.ALPHABET;
        var code = new StringBuilder(InvitationCodeValue.LENGTH);
        for (int i = 0; i < InvitationCodeValue.LENGTH; i++) {
            code.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return new InvitationCodeValue(code.toString());
    }
}
