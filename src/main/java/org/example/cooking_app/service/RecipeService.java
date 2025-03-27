package org.example.cooking_app.service;

import org.example.cooking_app.dto.CategoryDTO;
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
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecentSearchRepository recentSearchRepository;
    private final AttachmentRepository attachmentRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final CategoryRepository categoryRepository;

    public RecipeService(RecipeRepository recipeRepository, RecentSearchRepository recentSearchRepository, AttachmentRepository attachmentRepository, IngredientRepository ingredientRepository,
                         RecipeIngredientRepository recipeIngredientRepository, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.recentSearchRepository = recentSearchRepository;
        this.attachmentRepository = attachmentRepository;
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


    public RecipejonDTO getRecipe(Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));
        RecipejonDTO recipejonDTO = new RecipejonDTO();
        recipejonDTO.setName(recipe.getName());
        recipejonDTO.setDescription(recipe.getDescription());
        recipejonDTO.setSteps(recipe.getSteps());
        recipejonDTO.setDuration(recipe.getDuration());
        recipejonDTO.setPhotoId(recipe.getPhoto().getId());

        // Listlarni initsializatsiya qilamiz
        recipejonDTO.setCategoryIds(new ArrayList<>());
        recipejonDTO.setIngredientsOfRecipe(new ArrayList<>());

        for (Category category : recipe.getCategories()) {
            recipejonDTO.getCategoryIds().add(category.getId());
        }
        for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            RecipejonDTO.IngredientEntry entry = new RecipejonDTO.IngredientEntry();
            entry.setIngredientId(recipeIngredient.getIngredient().getId());
            entry.setQuantity(recipeIngredient.getQuantity());
            recipejonDTO.getIngredientsOfRecipe().add(entry);
        }
        return recipejonDTO;
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
            if(entry.getQuantity()!=0) {
                RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredient(ingredient)
                        .quantity(entry.getQuantity())
                        .build();
                recipeIngredients.add(recipeIngredient);
            }
        }
            recipeIngredientRepository.saveAll(recipeIngredients);
            return ResponseEntity.status(HttpStatus.CREATED).body("Recipe added successfully");
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

    public HttpEntity<?> updateRecipe(Integer recipeId, RecipejonDTO recipejondto, User user) {
        // 1. Mavjud recipe ni olish
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        // 2. (Ixtiyoriy) Foydalanuvchi recipe ustida o‘zgarish kiritish huquqiga ega ekanligini tekshirish
        if (!recipe.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Siz ushbu recipe ni yangilash huquqiga ega emassiz");
        }

        // 3. Duplicate ingredientlarni tekshirish
        Set<Integer> ingredientIds = new HashSet<>();
        for (RecipejonDTO.IngredientEntry entry : recipejondto.getIngredientsOfRecipe()) {
            if (!ingredientIds.add(entry.getIngredientId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Duplicate ingredient topildi: ingredientId = " + entry.getIngredientId());
            }
        }

        // 4. Recipe ning asosiy maydonlarini yangilash
        recipe.setName(recipejondto.getName());
        recipe.setDescription(recipejondto.getDescription());
        recipe.setDuration(recipejondto.getDuration());
        recipe.setSteps(new ArrayList<>(recipejondto.getSteps()));

        // Yangi link yaratish
        String link = recipejondto.getName().replace(" ", "_");
        recipe.setLink("app.Recipe.co/" + link);

        // 5. Agar photo o'zgargan bo'lsa, yangi Attachment ni yuklash
        if (!recipe.getPhoto().getId().equals(recipejondto.getPhotoId())) {
            Attachment attachment = attachmentRepository.findById(recipejondto.getPhotoId())
                    .orElseThrow(() -> new RuntimeException("Attachment not found"));
            recipe.setPhoto(attachment);
        }

        // 6. Kategoriyalarni yangilash
        List<Category> categories = categoryRepository.findAllById(recipejondto.getCategoryIds());
        recipe.setCategories(categories);

        // 7. Recipe ni yangilab saqlash
        recipeRepository.save(recipe);

        // 8. RecipeIngredient larni yangilash:
        //    Avval eski ingredientlarni o'chirib tashlaymiz (agar orphanRemoval qo'llanilgan bo'lsa, bu etarli)
        recipeIngredientRepository.deleteByRecipe(recipe); // repository da bunday metodni yarating

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (RecipejonDTO.IngredientEntry entry : recipejondto.getIngredientsOfRecipe()) {
            Ingredient ingredient = ingredientRepository.findById(entry.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found"));
            if (entry.getQuantity() != 0) {
                RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredient(ingredient)
                        .quantity(entry.getQuantity())
                        .build();
                recipeIngredients.add(recipeIngredient);
            }
        }
        recipeIngredientRepository.saveAll(recipeIngredients);

        // 9. Yangilangan recipe ni qaytaramiz
        return ResponseEntity.ok("Recipe successfully updated!");
    }

    public Recipe getRecipeById(Integer recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));
    }
}
