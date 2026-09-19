package com.gosqu.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TwoFactorRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6, max = 6) String code
) {}
