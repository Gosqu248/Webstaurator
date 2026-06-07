package com.gosqu.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmPasswordResetRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8, max = 72) String newPassword
) {}
