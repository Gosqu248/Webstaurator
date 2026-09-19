package com.gosqu.user.profile.mapper;

import com.gosqu.user.profile.UserProfile;
import com.gosqu.user.profile.dto.response.ProfileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileResponse toResponse(UserProfile profile);
}
