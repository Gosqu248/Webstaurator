package pl.urban.backend.config.security;

import lombok.Getter;

@Getter
public enum TokenPurpose {
    AUTH("auth"),
    PASSWORD_RESET("password_reset");

    private final String value;

    TokenPurpose(String value) {
        this.value = value;
    }

}
