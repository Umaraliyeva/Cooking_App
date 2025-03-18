package org.example.cooking_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Value;
import org.example.cooking_app.entity.Attachment;
import org.example.cooking_app.entity.Category;
import org.example.cooking_app.entity.Ingredient;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecipeDTO {
    private String name;
    private String description;
//    private String link;
    private Integer attachment_id;
    private Integer duration;
    private List<String> steps;
    private List<Integer> categoryIds;

    private List<IngredientEntry> ingredients; // ID + quantity

    @Data
    public static class IngredientEntry {
        private Integer ingredientId;
        private Integer quantity;
    }
}
