package com.gosqu.order.application.dto.request;

import com.gosqu.order.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status, String cancellationReason) {}
