package com.serenia.platform.iam.domain.model.commands;

/**
 * Intention of authenticating a user and opening a session.
 *
 * @param email      the email the user signs in with
 * @param password   the raw password, verified against the stored hash
 * @param deviceInfo optional description of the device
 */
public record SignInCommand(String email, String password, String deviceInfo) {
}
