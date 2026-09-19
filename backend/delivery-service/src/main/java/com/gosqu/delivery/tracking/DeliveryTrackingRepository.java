package com.gosqu.delivery.tracking;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryTrackingRepository extends MongoRepository<DeliveryTracking, String> {

    Optional<DeliveryTracking> findByOrderId(UUID orderId);

    GeoResults<DeliveryTracking> findByCurrentLocationNear(GeoJsonPoint point, Distance distance);
}
