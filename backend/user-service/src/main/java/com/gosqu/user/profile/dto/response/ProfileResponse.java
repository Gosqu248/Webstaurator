package com.gosqu.user.profile.dto.response;

import java.util.UUID;

public record ProfileResponse(
        UUID userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String avatarUrl
) {}
