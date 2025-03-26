package org.example.cooking_app.service;

import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.RecipeRepository;
import org.example.cooking_app.repo.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public UserService(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public HttpEntity<?> saveRecipe(User user, Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (user.getSavedRecipes().contains(recipe)) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body("Recipe allaqachon save qilingan");
        } else {
            // Agar retsept saqlanmagan bo‘lsa, uni saqlaymiz
            user.getSavedRecipes().add(recipe);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Recipe successfully saved!");
        }
    }

    public HttpEntity<?> unsaveRecipe(User user, Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        if (user.getSavedRecipes().contains(recipe)) {
            user.getSavedRecipes().removeIf(recipe1 -> recipe1.getId().equals(recipeId));
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Recipe successfully unsaved!");
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Recipe not saved!");
        }
    }

}
