package com.sondev.profile.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sondev.profile.dto.request.ProfileCreationRequest;
import com.sondev.profile.dto.request.ProfileUpdateRequest;
import com.sondev.profile.dto.response.UserProfileResponse;
import com.sondev.profile.entity.UserProfile;
import com.sondev.profile.mapper.UserProfileMapper;
import com.sondev.profile.repository.UserProfileRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileService(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        userProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse getUserProfile(String profileId) {
        UserProfile userProfile =
                userProfileRepository.findById(profileId).orElseThrow(() -> new RuntimeException("Profile not found"));

        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public void deleteProfile(String profileId) {
        userProfileRepository.deleteById(profileId);
    }

    public List<UserProfileResponse> getProfiles() {
        List<UserProfile> userProfiles = userProfileRepository.findAll();
        return userProfiles.stream()
                .map(profile -> userProfileMapper.toUserProfileResponse(profile))
                .toList();
    }

    public UserProfileResponse updateProfile(String profileId, ProfileUpdateRequest request) {
        UserProfile userProfile =
                userProfileRepository.findById(profileId).orElseThrow(() -> new RuntimeException("Profile not found"));

        userProfileMapper.updateUserProfile(userProfile, request);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }
}