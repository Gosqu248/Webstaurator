package com.gosqu.auth.user;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Embeddable
public record HashedPassword(String value) {

    private static final String BCRYPT_PREFIX_2A = "$2a$";
    private static final String BCRYPT_PREFIX_2B = "$2b$";
    private static final String BCRYPT_PREFIX_2Y = "$2y$";
    private static final int MIN_PLAIN_PASSWORD_LENGTH = 8;

    public HashedPassword {
        Objects.requireNonNull(value, "Password hash cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be blank");
        }
        if (!isBcryptHash(value)) {
            throw new IllegalArgumentException("Invalid password hash format - must be BCrypt encoded");
        }
    }

    public static HashedPassword fromPlainText(String plainPassword, PasswordEncoder encoder) {
        Objects.requireNonNull(plainPassword, "Plain password cannot be null");
        Objects.requireNonNull(encoder, "Password encoder cannot be null");

        if (plainPassword.length() < MIN_PLAIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PLAIN_PASSWORD_LENGTH + " characters long");
        }

        return new HashedPassword(encoder.encode(plainPassword));
    }

    public static HashedPassword fromHash(String hash) {
        return new HashedPassword(hash);
    }

    public boolean matches(String plainPassword, PasswordEncoder encoder) {
        if (plainPassword == null || plainPassword.isBlank()) {
            return false;
        }

        return encoder.matches(plainPassword, value);
    }

    private boolean isBcryptHash(String value) {
        return value.startsWith(BCRYPT_PREFIX_2A)
                || value.startsWith(BCRYPT_PREFIX_2B)
                || value.startsWith(BCRYPT_PREFIX_2Y);
    }

    @Override
    public @NotNull String toString() {
        return "HashedPassword[MASKED]";
    }
}
