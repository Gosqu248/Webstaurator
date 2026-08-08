package com.gosqu.user.profile;

import com.gosqu.user.profile.dto.request.UpdateProfileRequest;
import com.gosqu.user.profile.dto.response.ProfileResponse;
import com.gosqu.user.profile.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Transactional
    @Cacheable(value = "profile", key = "#userId")
    public ProfileResponse getProfile(UUID userId) {
        return profileRepository.findByUserId(userId)
                .map(profileMapper::toResponse)
                .orElseGet(() -> {
                    log.info("action=create_profile userId={}", userId);
                    UserProfile created = profileRepository.save(
                            UserProfile.builder().userId(userId).build()
                    );
                    return profileMapper.toResponse(created);
                });
    }

    @Transactional
    @CacheEvict(value = "profile", key = "#userId")
    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> UserProfile.builder().userId(userId).build());

        if (request.firstName() != null) profile.setFirstName(request.firstName());
        if (request.lastName() != null) profile.setLastName(request.lastName());
        if (request.phoneNumber() != null) profile.setPhoneNumber(request.phoneNumber());

        ProfileResponse response = profileMapper.toResponse(profileRepository.save(profile));
        log.info("action=update_profile userId={}", userId);
        return response;
    }
}
