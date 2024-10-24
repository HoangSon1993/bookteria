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
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/user")
    UserProfileResponse createProfile(@RequestBody ProfileCreationRequest request) {
        return userProfileService.createProfile(request);
    }

    @GetMapping("/user")
    List<UserProfileResponse> getUserProfile() {
        return userProfileService.getProfiles();
    }

    @GetMapping("/user/{profileId}")
    UserProfileResponse getProfile(@PathVariable String profileId) {
        return userProfileService.getUserProfile(profileId);
    }

    @PutMapping("/user/{profileId}")
    UserProfileResponse updateProfile(@PathVariable String profileId, @RequestBody ProfileUpdateRequest request) {
        return userProfileService.updateProfile(profileId, request);
    }

    @DeleteMapping("user/{profileId}")
    void deleteProfile(@PathVariable String profileId) {
        userProfileService.deleteProfile(profileId);
    }
}
