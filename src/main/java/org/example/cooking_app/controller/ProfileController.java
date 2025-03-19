package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cooking_app.service.ProfileService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile API", description = "Foydalanuvchi profili bilan ishlash uchun API")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(summary = "Foydalanuvchi profilini olish", description = "Berilgan userId bo‘yicha foydalanuvchi va recipe objectdan ma'lumotlarni qaytaradi")
    @GetMapping("/getCurrentUserProfile/{userId}")
    public HttpEntity<?> getCurrentUserProfile(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(profileService.getProfile(userId));
    }
}
