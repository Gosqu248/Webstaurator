package com.gosqu.user.media;

import com.gosqu.user.media.dto.response.AvatarUploadResponse;
import com.gosqu.user.media.dto.response.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users/me/avatar")
@RequiredArgsConstructor
public class UserAvatarController {

    private final UserAvatarService avatarService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvatarUploadResponse uploadAvatar(@RequestHeader("X-User-Id") UUID userId,
                                              @RequestParam("file") MultipartFile file) {
        return avatarService.uploadAvatar(userId, file);
    }

    @GetMapping("/url")
    public PresignedUrlResponse getAvatarUrl(@RequestHeader("X-User-Id") UUID userId) {
        return avatarService.getAvatarUrl(userId);
    }
}
