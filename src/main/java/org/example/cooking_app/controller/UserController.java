package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.RecipeRepository;
import org.example.cooking_app.repo.UserRepository;
import org.example.cooking_app.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Tag(name = "login qilgan Userning infolari ")
    @GetMapping()
    public HttpEntity<?> getUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        System.out.println("keldi::");
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

    @GetMapping("/is-saved/{recipeId}")
    public ResponseEntity<Boolean> isRecipeSaved(@PathVariable Integer recipeId,
                                                 @AuthenticationPrincipal User user) {
        boolean isSaved = userService.isRecipeSaved(user, recipeId);
        return ResponseEntity.ok(isSaved);

    }

    @Tag(name="follow qilinadi")
    @PostMapping("/follow/{recipeId}")
    public HttpEntity<?> follow(@PathVariable Integer recipeId, @AuthenticationPrincipal User user) {
        // Recipe ni olish
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        // Recipe egasini olish
        User recipeOwner = recipe.getUser();

        if (recipeOwner.equals(user)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You can't follow yourself");
        }
        Boolean follower = userRepository.isFollower(user.getId(), recipeId);
        // Agar foydalanuvchi allaqachon follow qilgan bo'lsa, xatolik qaytaramiz
        if (follower) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Foydalanuvchi allaqachon follow qilgan");
        }else {
            recipeOwner.getFollowers().add(user);
            user.getFollowings().add(recipeOwner);
            userRepository.save(user);
            userRepository.save(recipeOwner);
            return ResponseEntity.ok(userRepository.getFollowersByUserId(recipeOwner.getId()));
        }
    }

    @Tag(name="unfollow qilinadi")
    @DeleteMapping("/unfollow/{recipeId}")
    public HttpEntity<?> unfollow(@PathVariable Integer recipeId, @AuthenticationPrincipal User user) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));
        User recipeOwner = recipe.getUser();
        if (recipeOwner.equals(user)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You can't unfollow yourself");
        }
        Boolean follower = userRepository.isFollower(user.getId(), recipeId);
        if (follower) {
            recipeOwner.getFollowers().removeIf(followerjon -> followerjon.getId().equals(user.getId()));
            user.getFollowings().removeIf(following -> following.getId().equals(recipeOwner.getId()));

            userRepository.save(user);
            userRepository.save(recipeOwner);
            return ResponseEntity.ok(userRepository.getFollowersByUserId(recipeOwner.getId()));
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Foydalanuvchi follow qilmagan");
        }
    }
}
