package com.gosqu.restaurant.hours.dto;

import com.gosqu.restaurant.hours.DayOfWeek;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;

public record OpeningHoursRequest(
        @NotNull List<HoursEntry> hours
) {
    public record HoursEntry(
            @NotNull DayOfWeek dayOfWeek,
            @NotNull LocalTime openTime,
            @NotNull LocalTime closeTime
    ) {}
}
