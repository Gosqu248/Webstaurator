package com.gosqu.restaurant.hours;

import com.gosqu.restaurant.hours.dto.OpeningHoursRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/restaurants/{id}/hours")
@RequiredArgsConstructor
public class OpeningHoursController {

    private final OpeningHoursService openingHoursService;

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setOpeningHours(@RequestHeader("X-User-Id") UUID ownerId,
                                @PathVariable UUID id,
                                @RequestBody @Valid OpeningHoursRequest request) {
        openingHoursService.setOpeningHours(ownerId, id, request);
    }
}
