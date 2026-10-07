package com.serenia.platform.carecircle.domain.exceptions;

/**
 * Raised when the requester is not allowed to perform an operation on a care circle.
 */
public class CareCircleAccessDeniedException extends CareCircleDomainException {
    public CareCircleAccessDeniedException(String messageKey) {
        super(messageKey);
    }
}
