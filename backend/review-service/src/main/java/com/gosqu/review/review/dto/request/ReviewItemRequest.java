package com.gosqu.review.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ReviewItemRequest(
        @NotNull UUID menuItemId,
        @NotBlank @Size(max = 200) String name,
        @Min(1) @Max(5) int rating,
        @Size(max = 500) String comment
) {}
