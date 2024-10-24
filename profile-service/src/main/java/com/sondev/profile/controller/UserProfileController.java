package com.sondev.profile.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sondev.profile.dto.request.ProfileCreationRequest;
import com.sondev.profile.dto.request.ProfileUpdateRequest;
import com.sondev.profile.dto.response.UserProfileResponse;
import com.sondev.profile.service.UserProfileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/users")
    UserProfileResponse createProfile(@RequestBody ProfileCreationRequest request) {
        return userProfileService.createProfile(request);
    }

    @GetMapping("/users")
    List<UserProfileResponse> getUserProfile() {
        return userProfileService.getProfiles();
    }

    @GetMapping("/users/{profileId}")
    UserProfileResponse getProfile(@PathVariable String profileId) {
        return userProfileService.getUserProfile(profileId);
    }

    @PutMapping("/users/{profileId}")
    UserProfileResponse updateProfile(@PathVariable String profileId, @RequestBody ProfileUpdateRequest request) {
        return userProfileService.updateProfile(profileId, request);
    }

    @DeleteMapping("/users/{profileId}")
    void deleteProfile(@PathVariable String profileId) {
        userProfileService.deleteProfile(profileId);
    }
}
