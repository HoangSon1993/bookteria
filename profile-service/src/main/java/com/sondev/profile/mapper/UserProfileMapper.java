package com.sondev.profile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.sondev.profile.dto.request.ProfileCreationRequest;
import com.sondev.profile.dto.request.ProfileUpdateRequest;
import com.sondev.profile.dto.response.UserProfileResponse;
import com.sondev.profile.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile entity);

    void updateUserProfile(@MappingTarget UserProfile userProfile, ProfileUpdateRequest request);
}
