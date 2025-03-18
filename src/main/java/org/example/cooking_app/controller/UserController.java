package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @Tag(name = "login qilgan Userning infolari ")
    @GetMapping()
    public HttpEntity<?> getUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("fullname", user.getFullName());
        userData.put("attachmentId", user.getProfilePicture() != null ? user.getProfilePicture().getId() : null);
        userData.put("roles", user.getRoles());

        return ResponseEntity.ok(userData);
    }

    @PostMapping("/save-recipe/{recipeId}")
    public ResponseEntity<String> toggleSaveRecipe(@PathVariable Integer recipeId,
                                                   @AuthenticationPrincipal User user) {
        String status = userService.manageSaveAndUnSaveRecipe(user, recipeId);
        return ResponseEntity.ok(status);
    }

    // ✅ Retsept saqlangan yoki yo‘qligini tekshirish
    @GetMapping("/is-saved/{recipeId}")
    public ResponseEntity<Boolean> isRecipeSaved(@PathVariable Integer recipeId,
                                                 @AuthenticationPrincipal User user) {
        boolean isSaved = userService.isRecipeSaved(user, recipeId);
        return ResponseEntity.ok(isSaved);

    }
}
