package org.example.cooking_app.service;

import org.example.cooking_app.dto.IngredientDTO;
import org.example.cooking_app.dto.IngredientjonDTO;
import org.example.cooking_app.dto.RecipeDTO;
import org.example.cooking_app.dto.RecipejonDTO;
import org.example.cooking_app.entity.*;
import org.example.cooking_app.repo.*;
import jakarta.persistence.criteria.Join;
import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.repo.RecentSearchRepository;
import org.example.cooking_app.repo.RecipeRepository;
import org.springframework.data.jpa.domain.Specification;
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

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
        Specification<Recipe> spec = Specification.where(null);

        // 📌 POPULARITY bo‘yicha saralash
        if ("popularity".equalsIgnoreCase(timeFilter)) {
            List<Integer> popularRecipeIds = recentSearchRepository.findMostSearchedRecipeIds();
            if (!popularRecipeIds.isEmpty()) {
                spec = spec.and((root, query, criteriaBuilder) -> root.get("id").in(popularRecipeIds));
            }
        }

        // ⭐ RATE FILTER (likes >= rateFilter)
        if (rateFilter != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("likes"), rateFilter));
        }

        // 📂 CATEGORY FILTER
        if (categoryId != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Object, Object> categoryJoin = root.join("categories");
                return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
            });
        }

        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt"));
        return recipeRepository.findAll(spec, pageable).getContent();
    }


    private org.springframework.data.domain.Sort getSorting(String timeFilter) {
        if ("newest".equalsIgnoreCase(timeFilter)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        } else if ("oldest".equalsIgnoreCase(timeFilter)) {
            return Sort.by(Sort.Direction.ASC, "createdAt");
        }
        return Sort.by(Sort.Direction.DESC, "createdAt");// Popularity sorting yuqorida hal qilindi
    }


    public Recipe getRecipeById(Integer recipeId) {
        return recipeRepository.findById(recipeId).orElse(null);
    }

    public HttpEntity<?> addRecipe(RecipejonDTO recipejondto, User user) throws IOException {
        // Avvalo duplicate ingredientlarni tekshirib chiqamiz:
        Set<Integer> ingredientIds = new HashSet<>();
        for (RecipejonDTO.IngredientEntry entry : recipejondto.getIngredientsOfRecipe()) {
            if (!ingredientIds.add(entry.getIngredientId())) {
                // Duplicate ingredient topildi, shuning uchun umuman recipe yaratilmasin
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Duplicate ingredient topildi: ingredientId = " + entry.getIngredientId());
            }
        }

        // Agar duplicate topilmasa, recipe yaratishni davom ettiramiz:
        String link = recipejondto.getName().replace(" ", "_");
        Attachment attachment = attachmentRepository.findById(recipejondto.getPhotoId()).orElseThrow(()->new RuntimeException("Attachment not found"));
            Recipe recipe = Recipe.builder().
                    name(recipejondto.getName()).
                    description(recipejondto.getDescription())
                    .link("app.Recipe.co/"+link)
                    .user(user)
                    .photo(attachment)
                    .duration(recipejondto.getDuration())
                    .steps(new ArrayList<>(recipejondto.getSteps()))
                    .build();

        List<Category> categories = categoryRepository.findAllById(recipejondto.getCategoryIds());
        recipe.setCategories(categories);

        recipeRepository.save(recipe);

        // RecipeIngredient larni tayyorlaymiz
            List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (RecipejonDTO.IngredientEntry entry : recipejondto.getIngredientsOfRecipe()) {
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
        List<IngredientjonDTO> ingredientDTOS = recipe.getRecipeIngredients()
                .stream()
                .map(ri -> {
                    // ingredient obyekti ichidan kerakli maydonlarni olib, DTO ga aylantiramiz
                    Ingredient ing = ri.getIngredient();
                    return new IngredientjonDTO(ing.getPhoto(),ing.getName(),ri.getQuantity());
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
