package com.serenia.platform.carecircle.application.internal.outboundservices.codes;

import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeValue;

/**
 * Outbound port for the random generation of invitation codes.
 */
public interface InvitationCodeGenerator {

    /**
     * Generates a new random code. Uniqueness is checked by the caller.
     *
     * @return the generated code
     */
    InvitationCodeValue generate();
}
