package org.example.cooking_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.example.cooking_app.entity.Attachment;
import org.example.cooking_app.entity.Category;
import org.example.cooking_app.entity.Ingredient;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer photoId; //Attachment ni id sini olib keladi faqat
    private Integer duration;
    private Integer likes;
    private String link;
    private List<String> steps;
    List<IngredientDTO> ingredients;
    List<CategoryDTO> categories;
    private LocalDateTime createdAt;

}
