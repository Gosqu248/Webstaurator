package com.gosqu.review.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RespondToReviewRequest(
        @NotBlank @Size(max = 1000) String message
) {}
