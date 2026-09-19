package com.gosqu.order.infrastructure.persistence.entity;

import com.gosqu.order.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID restaurantId;

    @Column(nullable = false)
    private String restaurantName;

    @Column(nullable = false)
    private String deliveryStreet;

    @Column(nullable = false)
    private String deliveryCity;

    private String deliveryPostalCode;

    @Column(nullable = false)
    private String deliveryCountry;

    private Double deliveryLatitude;
    private Double deliveryLongitude;

    @Column(nullable = false)
    private BigDecimal deliveryFeeAmount;

    @Column(nullable = false)
    private String deliveryFeeCurrency;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String totalCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String cancellationReason;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItemJpaEntity> items = new ArrayList<>();
}
