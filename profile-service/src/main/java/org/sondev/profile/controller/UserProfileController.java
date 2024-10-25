package org.sondev.profile.controller;

import java.util.List;

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

    @GetMapping("/users")
    List<UserProfileResponse> getUserProfile() {
        return userProfileService.getProfiles();
    }
}
