package org.example.cooking_app.dto;


import lombok.Data;

import java.util.List;

@Data
public class RecipejonDTO {
    private String name;
    private String description;
    private Integer photoId;
    private Integer duration;
    private List<String> steps;
    private List<Integer> categoryIds;

    private List<IngredientEntry> ingredientsOfRecipe; // ID + quantity

    @Data
    public static class IngredientEntry {
        private Integer ingredientId;
        private Integer quantity;
    }
}
