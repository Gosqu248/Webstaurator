package com.gosqu.auth.user;

import jakarta.persistence.Embeddable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        Objects.requireNonNull(value, "Email value cannot be null");
        value = value.toLowerCase().trim();

        if (value.isBlank()) {
            throw new IllegalArgumentException("Email value cannot be blank");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String getDomain() {
        return value().substring(value().indexOf("@") + 1);
    }

    @Override
    public @NonNull String toString() {
        return value();
    }
}
