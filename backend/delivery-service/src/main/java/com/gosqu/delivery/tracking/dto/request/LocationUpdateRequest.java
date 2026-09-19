package com.gosqu.delivery.tracking.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record LocationUpdateRequest(
        @DecimalMin("-180") @DecimalMax("180") double longitude,
        @DecimalMin("-90") @DecimalMax("90") double latitude
) {
}
