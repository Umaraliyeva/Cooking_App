package org.example.cooking_app.controller;

import org.example.cooking_app.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping
    public String getCurrentUserProfile(@AuthenticationPrincipal User user) {

    }
}
