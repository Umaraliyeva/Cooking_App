package org.example.cooking_app.service;

import lombok.RequiredArgsConstructor;
import org.example.cooking_app.dto.*;
import org.example.cooking_app.repo.CategoryRepository;
import org.example.cooking_app.repo.IngredientRepository;
import org.example.cooking_app.repo.RecipeRepository;
import org.example.cooking_app.repo.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final CategoryRepository categoryRepository;

    public HttpEntity<?> getProfile(Integer userId) {
        List<Object[]> results = userRepository.getProfileDTOById(userId);

        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        }

        Object[] row = results.get(0); // List ichidan birinchi elementni olamiz

        ProfileDTO profileDTO = new ProfileDTO(
                ((Number) row[0]).intValue(), // id
                (String) row[1], // fullName (query bo‘yicha 2-ustun)
                row[2] != null ? ((Number) row[2]).intValue() : null, // profilePictureId
                (String) row[3], // info
                ((Number) row[4]).intValue(), // followersCount
                ((Number) row[5]).intValue(), // followingCount
                ((Number) row[6]).intValue(), // recipesCount
                (String) row[7] // profession
        );


        // ✅ Recipe larni olib kelamiz
        List<Object[]> recipeObjects = recipeRepository.getRecipesByUserId(userId);
        List<RecipeDTO> recipes = mapToRecipeDTO(recipeObjects);

        // ✅ Ingredient va Category larni har bir recipe uchun qo‘shamiz
        for (RecipeDTO recipe : recipes) {
            List<Object[]> ingredientObjects = ingredientRepository.getIngredientsByRecipeId(recipe.getId());
            List<IngredientDTO> ingredients = mapToIngredientDTO(ingredientObjects);

            List<Object[]> categoryObjects = categoryRepository.getCategoriesByRecipeId(recipe.getId());
            List<CategoryDTO> categories = mapToCategoryDTO(categoryObjects);

            recipe.setIngredients(ingredients);
            recipe.setCategories(categories);
        }

       // profileDTO.setRecipes(recipes);
        ProfileWithRecipeDTO profileWithRecipeDTO = new ProfileWithRecipeDTO();
        profileWithRecipeDTO.setProfile(profileDTO);
        profileWithRecipeDTO.setRecipe(recipes);
        return ResponseEntity.ok(profileWithRecipeDTO);
    }

    // 🔹 Recipe obyektlarini DTO ga aylantirish
    private List<RecipeDTO> mapToRecipeDTO(List<Object[]> results) {
        for (Object[] row : results) {
            System.out.println("createdAt: " + row[8]);
        }

        return results.stream().map(row -> new RecipeDTO(
                ((Number) row[0]).intValue(), // id
                (String) row[1], // name
                (String) row[2], // description
                row[3] != null ? ((Number) row[3]).intValue() : null, // photoId
                row[4] != null ? ((Number) row[4]).intValue() : null, // duration
                row[5] != null ? ((Number) row[5]).intValue() : null, // likes
                (String) row[6], // link
                row[7] != null ? List.of(((String) row[7]).split(",")) : List.of(), // steps
                null, // ingredients (keyin qo'shamiz)
                null, // categories (keyin qo'shamiz)
                row[8] != null ?
                        (row[8] instanceof java.sql.Timestamp
                                ? ((java.sql.Timestamp) row[8]).toLocalDateTime()
                                : LocalDateTime.parse(row[8].toString()))
                        : null // createdAt
        )).toList();
    }


    // 🔹 Ingredient obyektlarini DTO ga aylantirish
    private List<IngredientDTO> mapToIngredientDTO(List<Object[]> results) {
        return results.stream().map(row -> new IngredientDTO(
                ((Number) row[0]).intValue(), // id
                (String) row[1], // name
                row[2] != null ? ((Number) row[2]).intValue() : null, // photoId
                row[3] != null ? ((Number) row[3]).intValue() : null // quantity
        )).toList();
    }

    // 🔹 Category obyektlarini DTO ga aylantirish
    private List<CategoryDTO> mapToCategoryDTO(List<Object[]> results) {
        return results.stream().map(row -> new CategoryDTO(
                ((Number) row[0]).intValue(), // id
                (String) row[1] // name
        )).toList();
    }
}
