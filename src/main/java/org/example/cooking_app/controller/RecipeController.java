package org.example.cooking_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.cooking_app.dto.RecipejonDTO;
import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.RecipeRating;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.CommentRepository;
import org.example.cooking_app.repo.RecentSearchRepository;
import org.example.cooking_app.repo.RecipeRatingRepository;
import org.example.cooking_app.repo.RecipeRepository;
import org.example.cooking_app.service.RecentSearchService;
import org.example.cooking_app.service.RecipeService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@MultipartConfig
public class RecipeController {

    private final RecipeService recipeService;
    private final RecentSearchService recentSearchService;
    private final RecentSearchRepository recentSearchRepository;
    private final ObjectMapper jacksonObjectMapper;
    private final RecipeRepository recipeRepository;
    private final RecipeRatingRepository recipeRatingRepository;
    private final CommentRepository commentRepository;


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
    @GetMapping("/search/{recipeId}")//o'zgartirish
    public ResponseEntity<?> getSearchedRecipeById(@PathVariable Integer recipeId,@AuthenticationPrincipal User user) {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        recentSearchService.saveRecentSearch(user, recipe);
        return ResponseEntity.status(201).body(recipeService.getRecipeById(recipeId));
    }

    @Tag(name = "user birinchi kirganida chiqib turgan retseptlardan birortasini tanlaganda id keladi")
    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeById(@PathVariable Integer recipeId) {
       return ResponseEntity.status(201).body(recipeService.getRecipe(recipeId));

    }

    @SneakyThrows
    @Tag(name = "recipe add qilish")
    @PostMapping
    public HttpEntity<?> addRecipe(
                                   @RequestBody RecipejonDTO recipejonDTO,
                                   @AuthenticationPrincipal User user) throws IOException {
        return ResponseEntity.status(201).body(recipeService.addRecipe(recipejonDTO,user));
    }



    @Tag(name = "recipeni ostidagi ingredient btn bosilganda unga tegishli ingredientlar keladi")
    @GetMapping("/ingredients/{recipeId}")
    public HttpEntity<?> getIngredientById(@PathVariable Integer recipeId) {
        return ResponseEntity.status(201).body(recipeService.getIngredientsByRecipeId(recipeId));
    }


    @Tag(name = "recipega tegishli proceduralar(steplar)")
    @GetMapping("/step/{recipeId}")
    public HttpEntity<?> getStepById(@PathVariable Integer recipeId) {
        return ResponseEntity.status(201).body(recipeService.getStepsByRecipeId(recipeId));
    }

    @Tag(name = "recipeni update qilish")
    @PutMapping("/update/{recipeId}")
    @Transactional
    public HttpEntity<?> updateRecipe(@PathVariable Integer recipeId, @RequestBody RecipejonDTO recipejonDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(201).body(recipeService.updateRecipe(recipeId,recipejonDTO,user));
    }

    @Tag(name = "recipe delete qilish")
    @DeleteMapping("/delete/{recipeId}")
    public HttpEntity<?> deleteRecipe(@PathVariable Integer recipeId, @AuthenticationPrincipal User user) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));
        if (recipe.getUser().equals(user)){
            recipeRepository.delete(recipe);
            return ResponseEntity.status(200).body("Recipe deleted");
        }else {
            return ResponseEntity.status(404).body("This is not the recipe for you.");
        }
    }


    @Tag(name = "recipega rating berish")
    @PostMapping("/rate/{recipeId}")
    public HttpEntity<?> rateRecipe(@PathVariable Integer recipeId,@RequestParam Integer rating, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recipeService.saveRating(recipeId,user,rating));
    }
}
