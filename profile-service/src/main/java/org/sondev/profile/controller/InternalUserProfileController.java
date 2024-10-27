package org.sondev.profile.controller;

import java.util.List;

import org.sondev.profile.dto.request.ProfileCreationRequest;
import org.sondev.profile.dto.request.ProfileUpdateRequest;
import org.sondev.profile.dto.response.UserProfileResponse;
import org.sondev.profile.service.UserProfileService;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class InternalUserProfileController {
    private final UserProfileService userProfileService;

    public InternalUserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/internal/users")
    UserProfileResponse createProfile(@RequestBody ProfileCreationRequest request) {
        return userProfileService.createProfile(request);
    }

    @GetMapping("/internal/users")
    List<UserProfileResponse> getUserProfile() {
        return userProfileService.getProfiles();
    }

    @GetMapping("/internal/users/{profileId}")
    UserProfileResponse getProfile(@PathVariable String profileId) {
        return userProfileService.getUserProfile(profileId);
    }

    @PutMapping("/internal/users/{profileId}")
    UserProfileResponse updateProfile(@PathVariable String profileId, @RequestBody ProfileUpdateRequest request) {
        return userProfileService.updateProfile(profileId, request);
    }

    @DeleteMapping("/internal/users/{profileId}")
    void deleteProfile(@PathVariable String profileId) {
        userProfileService.deleteProfile(profileId);
    }
}
