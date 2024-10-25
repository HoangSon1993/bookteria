package org.sondev.profile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.sondev.profile.dto.request.ProfileCreationRequest;
import org.sondev.profile.dto.request.ProfileUpdateRequest;
import org.sondev.profile.dto.response.UserProfileResponse;
import org.sondev.profile.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile entity);

    void updateUserProfile(@MappingTarget UserProfile userProfile, ProfileUpdateRequest request);
}
