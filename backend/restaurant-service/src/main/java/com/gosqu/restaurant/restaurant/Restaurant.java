package com.gosqu.restaurant.restaurant;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID ownerId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CuisineType cuisineType;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    private Double latitude;
    private Double longitude;

    private String phoneNumber;
    private String logoUrl;
    private String bannerUrl;

    @Column(nullable = false)
    private Boolean isActive = true;

    private Double avgRating = 0.0;

    private Integer deliveryTimeMin;

    @Column(precision = 6, scale = 2)
    private BigDecimal deliveryFee;

    @Column(precision = 8, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

}
