package com.gosqu.review.review.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateReviewRequest(
        @NotNull UUID orderId,
        @Min(1) @Max(5) int restaurantRating,
        @Size(max = 1000) String restaurantComment,
        @NotNull @Valid List<ReviewItemRequest> items
) {}
