package com.gosqu.user.profile.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 60) String firstName,
        @Size(max = 60) String lastName,
        @Pattern(regexp = "^[+]?[0-9 \\-()]{7,20}$") String phoneNumber
) {}
