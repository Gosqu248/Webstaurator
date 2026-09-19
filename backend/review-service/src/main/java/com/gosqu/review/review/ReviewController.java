package com.gosqu.review.review;

import com.gosqu.review.common.exception.ForbiddenException;
import com.gosqu.review.review.dto.request.CreateReviewRequest;
import com.gosqu.review.review.dto.request.RespondToReviewRequest;
import com.gosqu.review.review.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private static final String ROLE_RESTAURANT_ADMIN = "RESTAURANT_ADMIN";
    private static final String ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN";

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(@RequestHeader("X-User-Id") UUID customerId,
                                  @RequestBody @Valid CreateReviewRequest request) {
        return reviewService.create(customerId, request);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public Page<ReviewResponse> getForRestaurant(@PathVariable UUID restaurantId,
                                                  @RequestParam(defaultValue = "0") @Min(0) int page,
                                                  @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        return reviewService.getForRestaurant(restaurantId, PageRequest.of(page, size));
    }

    @GetMapping("/my")
    public List<ReviewResponse> getMyReviews(@RequestHeader("X-User-Id") UUID customerId) {
        return reviewService.getMyReviews(customerId);
    }

    // Rola z nagłówka gatewaya to pierwszy filtr; faktyczne właścicielstwo restauracji jest
    // weryfikowane w ReviewService.respond() przez wywołanie restaurant-service (GET
    // /internal/restaurants/{id}/owner, chronione X-Internal-Key).
    @PostMapping("/{id}/response")
    public ReviewResponse respond(@RequestHeader("X-User-Id") UUID ownerId,
                                   @RequestHeader("X-User-Role") String role,
                                   @PathVariable String id,
                                   @RequestBody @Valid RespondToReviewRequest request) {
        requireRole(role, ROLE_RESTAURANT_ADMIN);
        return reviewService.respond(ownerId, id, request.message());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader("X-User-Role") String role, @PathVariable String id) {
        requireRole(role, ROLE_SYSTEM_ADMIN);
        reviewService.delete(id);
    }

    private void requireRole(String actual, String required) {
        if (!required.equals(actual)) {
            throw new ForbiddenException();
        }
    }
}
