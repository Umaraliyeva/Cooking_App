package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.RecentSearchRepository;
import org.example.cooking_app.service.RecentSearchService;
import org.example.cooking_app.service.RecipeService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecentSearchService recentSearchService;
    private final RecentSearchRepository recentSearchRepository;


    @Tag(name = "category id bo'yicha sort qilingan recipe lar")
    @GetMapping("/category/{categoryId}")
    public HttpEntity<?>getRecipesByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.status(200).body(recipeService.getRecipesByCategory(categoryId));
    }


    @Tag(name = " bugundan boshlab ohirgi 2 kun ichida creat qilingan yangi recipe larni olib keladi")
    @GetMapping("/newRecipes")
    public HttpEntity<?> getRecentRecipes(){
        return ResponseEntity.status(200).body(recipeService.getRecipesFromLastTwoDays());
    }


    @GetMapping
    public HttpEntity<?> getRecipes(){
        return ResponseEntity.status(200).body(recipeService.getRecipes());
    }
   @Tag(name = "recipe ni name yoki description bo'yicha search qiladi ")
    @GetMapping("/search")
    public ResponseEntity<?> searchRecipes(@RequestParam String search, @AuthenticationPrincipal User user) {
        List<Recipe> foundRecipes = recipeService.searchRecipes(search);
        return ResponseEntity.ok(foundRecipes);
    }

    @Tag(name = "time(all,newest,oldest,popularity=eng ko'p search qilingan),rate(1 dan 5), category  shular bo'yicha filter")
    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredRecipes(
            @RequestParam(required = false, defaultValue = "all") String timeFilter,
            @RequestParam(required = false) Integer rateFilter,
            @RequestParam(required = false) Integer categoryId) {

        List<Recipe> recipes = recipeService.filterRecipes(timeFilter, rateFilter, categoryId);
        return ResponseEntity.status(200).body(recipes);
    }
    @Tag(name = "user search, filter qilganda recipe ni ustiga bossa recipe id keladi , va recentSearch ga o'sha boskan recipe qo'shiladi ")
    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeById(@PathVariable Integer recipeId,@AuthenticationPrincipal User user) {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        return ResponseEntity.status(201).body(recentSearchService.saveRecentSearch(user, recipe));
    }

}
