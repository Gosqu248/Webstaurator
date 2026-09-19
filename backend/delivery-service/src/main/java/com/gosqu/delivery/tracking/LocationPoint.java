package com.gosqu.delivery.tracking;

import java.time.Instant;

public record LocationPoint(double longitude, double latitude, Instant timestamp) {
}
