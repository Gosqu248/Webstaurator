package com.gosqu.restaurant.media;

import com.gosqu.restaurant.media.dto.response.LogoUploadResponse;
import com.gosqu.restaurant.media.dto.response.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/restaurants/{id}/logo")
@RequiredArgsConstructor
public class RestaurantMediaController {

    private final RestaurantMediaService mediaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogoUploadResponse uploadLogo(@RequestHeader("X-User-Id") UUID ownerId,
                                          @PathVariable UUID id,
                                          @RequestParam("file") MultipartFile file) {
        return mediaService.uploadLogo(ownerId, id, file);
    }

    @GetMapping("/url")
    public PresignedUrlResponse getLogoUrl(@PathVariable UUID id) {
        return mediaService.getLogoUrl(id);
    }
}
