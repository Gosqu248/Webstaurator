package com.gosqu.review.review;

import com.gosqu.review.client.RestaurantClient;
import com.gosqu.review.common.exception.ForbiddenException;
import com.gosqu.review.eligibility.ReviewableOrder;
import com.gosqu.review.eligibility.ReviewableOrderRepository;
import com.gosqu.review.messaging.producer.RatingEventPublisher;
import com.gosqu.review.review.dto.request.CreateReviewRequest;
import com.gosqu.review.review.dto.request.ReviewItemRequest;
import com.gosqu.review.review.dto.response.ReviewResponse;
import com.gosqu.review.review.exception.OrderAlreadyReviewedException;
import com.gosqu.review.review.exception.OrderNotEligibleForReviewException;
import com.gosqu.review.review.exception.ReviewNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final String REVIEWS_COLLECTION = "reviews";

    private final ReviewRepository reviewRepository;
    private final ReviewableOrderRepository reviewableOrderRepository;
    private final RatingEventPublisher ratingEventPublisher;
    private final MongoTemplate mongoTemplate;
    private final RestaurantClient restaurantClient;

    public ReviewResponse create(UUID customerId, CreateReviewRequest request) {
        ReviewableOrder reviewableOrder = reviewableOrderRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new OrderNotEligibleForReviewException(request.orderId()));

        if (!reviewableOrder.customerId().equals(customerId)) {
            throw new ForbiddenException();
        }
        if (reviewableOrder.reviewed()) {
            throw new OrderAlreadyReviewedException(request.orderId());
        }

        List<ReviewItem> items = request.items().stream()
                .map(this::toReviewItem)
                .toList();

        Review review = Review.create(request.orderId(), customerId, reviewableOrder.restaurantId(),
                request.restaurantRating(), request.restaurantComment(), items);
        Review saved = reviewRepository.save(review);

        reviewableOrderRepository.save(reviewableOrder.markReviewed());

        publishRatingRecalculated(reviewableOrder.restaurantId());

        log.info("action=review_created orderId={} restaurantId={} rating={}",
                request.orderId(), reviewableOrder.restaurantId(), request.restaurantRating());

        return ReviewResponse.from(saved);
    }

    public Page<ReviewResponse> getForRestaurant(UUID restaurantId, Pageable pageable) {
        return reviewRepository.findByRestaurantIdAndVisibleTrue(restaurantId, pageable)
                .map(ReviewResponse::from);
    }

    public List<ReviewResponse> getMyReviews(UUID customerId) {
        return reviewRepository.findByCustomerId(customerId).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public ReviewResponse respond(UUID ownerId, String reviewId, String message) {
        Review review = findOrThrow(reviewId);

        UUID actualOwnerId = restaurantClient.getOwnerId(review.restaurantId());
        if (actualOwnerId == null || !actualOwnerId.equals(ownerId)) {
            throw new ForbiddenException();
        }

        Review updated = review.withReply(ReviewReply.of(ownerId, message));
        return ReviewResponse.from(reviewRepository.save(updated));
    }

    public void delete(String reviewId) {
        Review review = findOrThrow(reviewId);
        reviewRepository.save(review.hidden());
        publishRatingRecalculated(review.restaurantId());
        log.info("action=review_hidden reviewId={} restaurantId={}", reviewId, review.restaurantId());
    }

    private Review findOrThrow(String reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    private ReviewItem toReviewItem(ReviewItemRequest r) {
        return new ReviewItem(r.menuItemId(), r.name(), r.rating(), r.comment());
    }

    private void publishRatingRecalculated(UUID restaurantId) {
        double newAvg = recalculateAvgRating(restaurantId);
        ratingEventPublisher.publishRatingUpdated(restaurantId, newAvg);
    }

    /**
     * MongoDB aggregation pipeline: match visible reviews for the restaurant, then average
     * restaurantRating in a single $group stage. Equivalent SQL: SELECT AVG(restaurant_rating)
     * FROM reviews WHERE restaurant_id = ? AND visible = true.
     */
    private double recalculateAvgRating(UUID restaurantId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("restaurantId").is(restaurantId).and("visible").is(true)),
                Aggregation.group().avg("restaurantRating").as("avg")
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, REVIEWS_COLLECTION, Document.class);
        Document result = results.getUniqueMappedResult();
        return result != null ? result.getDouble("avg") : 0.0;
    }
}
