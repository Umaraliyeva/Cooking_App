package org.example.cooking_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
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
