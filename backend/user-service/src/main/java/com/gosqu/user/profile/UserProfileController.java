package com.gosqu.user.profile;

import com.gosqu.user.profile.dto.request.UpdateProfileRequest;
import com.gosqu.user.profile.dto.response.ProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private final UserProfileService profileService;

    @GetMapping("/me")
    public ProfileResponse getProfile(@RequestHeader("X-User-Id") UUID userId) {
        return profileService.getProfile(userId);
    }

    @PutMapping("/me")
    public ProfileResponse updateProfile(@RequestHeader("X-User-Id") UUID userId,
                                         @RequestBody @Valid UpdateProfileRequest request) {
        return profileService.updateProfile(userId, request);
    }
}
