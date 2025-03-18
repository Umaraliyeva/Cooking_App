package org.example.cooking_app.service;

import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.repo.RecentSearchRepository;
import org.example.cooking_app.repo.RecipeRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecentSearchRepository recentSearchRepository;

    public RecipeService(RecipeRepository recipeRepository, RecentSearchRepository recentSearchRepository) {
        this.recipeRepository = recipeRepository;
        this.recentSearchRepository = recentSearchRepository;
    }

    public HttpEntity<?> getRecipesByCategory(Integer categoryId) {
        List<Recipe> recipes = recipeRepository.findByCategories_Id(categoryId);
        return ResponseEntity.ok(recipes);
    }

    public HttpEntity<?> getRecipesFromLastTwoDays() {
        // 23-fevralning 00:00:00 dan bugungi kunga qadar
        LocalDateTime fromTime = LocalDate.now().minusDays(2).atStartOfDay(); // 23-fevral 00:00:00
        LocalDateTime toTime = LocalDateTime.now(); // bugun

        List<Recipe> recipes = recipeRepository.findRecipesInDateRange(fromTime, toTime);
        return ResponseEntity.ok(recipes);
    }

    public HttpEntity<?> getRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return ResponseEntity.ok(recipes);
    }

    public List<Recipe> searchRecipes(String search) {
        return  recipeRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
    }

    public List<Recipe> filterRecipes(String timeFilter, Integer rateFilter, Integer categoryId) {
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;
        List<Integer> popularRecipeIds = null;

        // 🕒 TIME FILTER
        if ("newest".equalsIgnoreCase(timeFilter)) {
            fromDate = LocalDateTime.now().minusDays(7);
        } else if ("oldest".equalsIgnoreCase(timeFilter)) {
            toDate = LocalDateTime.now().minusYears(1); // 7 jkundan tashqari hammasi oldest bo'ladi
        } else if ("popularity".equalsIgnoreCase(timeFilter)) {
            popularRecipeIds = recentSearchRepository.findMostSearchedRecipeIds();
        }

        // 📥 FILTER CALL
        return recipeRepository.filterRecipes(fromDate, toDate, popularRecipeIds, rateFilter, categoryId, timeFilter);
    }

    public Recipe getRecipeById(Integer recipeId) {
        return recipeRepository.findById(recipeId).orElse(null);
    }
}
