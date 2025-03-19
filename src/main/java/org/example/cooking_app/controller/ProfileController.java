package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.service.ProfileService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/getCurrentUserProfile")
    @Tag(name = "Profile", description = "Profile uchun user va recipe objectdan malumotlarni olib keladi")
    public HttpEntity<?> getCurrentUserProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(profileService.getProfile(user.getId()));
    }
}
