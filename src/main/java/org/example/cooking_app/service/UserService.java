package org.example.cooking_app.service;

import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.RecipeRepository;
import org.example.cooking_app.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public UserService(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public String manageSaveAndUnSaveRecipe(User user, Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (user.getSavedRecipes().contains(recipe)) {
            // Agar retsept allaqachon saqlangan bo‘lsa, uni o‘chirib tashlaymiz
            user.getSavedRecipes().remove(recipe);
            userRepository.save(user);
            return "unsave"; // Frontendga "unsave" statusini qaytarish
        } else {
            // Agar retsept saqlanmagan bo‘lsa, uni saqlaymiz
            user.getSavedRecipes().add(recipe);
            userRepository.save(user);
            return "save"; // Frontendga "save" statusini qaytarish
        }
    }

    public boolean isRecipeSaved(User user, Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        return user.getSavedRecipes().contains(recipe);
    }

}
