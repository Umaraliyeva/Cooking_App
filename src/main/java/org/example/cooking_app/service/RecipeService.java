package org.example.cooking_app.service;

import org.example.cooking_app.dto.IngredientDTO;
import org.example.cooking_app.dto.RecipeDTO;
import org.example.cooking_app.entity.*;
import org.example.cooking_app.repo.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecentSearchRepository recentSearchRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final CategoryRepository categoryRepository;

    public RecipeService(RecipeRepository recipeRepository, RecentSearchRepository recentSearchRepository, AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository, IngredientRepository ingredientRepository,
                         RecipeIngredientRepository recipeIngredientRepository, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.recentSearchRepository = recentSearchRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.categoryRepository = categoryRepository;
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
            toDate = LocalDateTime.now().minusYears(1);
        } else if ("popularity".equalsIgnoreCase(timeFilter)) {
            popularRecipeIds = recentSearchRepository.findMostSearchedRecipeIds();
        }

        // 📥 FILTER CALL
        return recipeRepository.filterRecipes(fromDate, toDate, popularRecipeIds, rateFilter, categoryId, timeFilter);
    }

    public Recipe getRecipeById(Integer recipeId) {
        return recipeRepository.findById(recipeId).orElse(null);
    }

    public HttpEntity<?> addRecipe(RecipeDTO recipedto, User user) throws IOException {
        // Avvalo duplicate ingredientlarni tekshirib chiqamiz:
        Set<Integer> ingredientIds = new HashSet<>();
        for (RecipeDTO.IngredientEntry entry : recipedto.getIngredients()) {
            if (!ingredientIds.add(entry.getIngredientId())) {
                // Duplicate ingredient topildi, shuning uchun umuman recipe yaratilmasin
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Duplicate ingredient topildi: ingredientId = " + entry.getIngredientId());
            }
        }

        // Agar duplicate topilmasa, recipe yaratishni davom ettiramiz:
        String link = recipedto.getName().replace(" ", "_");
        Attachment attachment = attachmentRepository.findById(recipedto.getAttachment_id()).orElseThrow(()->new RuntimeException("Attachment not found"));
            Recipe recipe = Recipe.builder().
                    name(recipedto.getName()).
                    description(recipedto.getDescription())
                    .link("app.Recipe.co/"+link)
                    .user(user)
                    .photo(attachment)
                    .duration(recipedto.getDuration())
                    .steps(new ArrayList<>(recipedto.getSteps()))
                    .build();

        List<Category> categories = categoryRepository.findAllById(recipedto.getCategoryIds());
        recipe.setCategories(categories);

        recipeRepository.save(recipe);

        // RecipeIngredient larni tayyorlaymiz
            List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (RecipeDTO.IngredientEntry entry : recipedto.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findById(entry.getIngredientId()).orElseThrow(() -> new RuntimeException("Ingredient not found"));
            RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                    .recipe(recipe)
                    .ingredient(ingredient)
                    .quantity(entry.getQuantity())
                    .build();
            recipeIngredients.add(recipeIngredient);
        }
            recipeIngredientRepository.saveAll(recipeIngredients);
            return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
    }

    public HttpEntity<?> getIngredientsByRecipeId(Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));
        // RecipeIngredient obyektlaridan ingredient obyektlarini olish
        List<IngredientDTO> ingredientDTOS = recipe.getRecipeIngredients()
                .stream()
                .map(ri -> {
                    // ingredient obyekti ichidan kerakli maydonlarni olib, DTO ga aylantiramiz
                    Ingredient ing = ri.getIngredient();
                    return new IngredientDTO(ing.getPhoto(),ing.getName(),ri.getQuantity());
                })
                .distinct() // Agar takroriy bo'lsa, bitta nusxasi olinadi
                .collect(Collectors.toList());
        return ResponseEntity.ok(ingredientDTOS);
    }

    public HttpEntity<?> getStepsByRecipeId(Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));
        return ResponseEntity.ok(recipe.getSteps());
    }
}
