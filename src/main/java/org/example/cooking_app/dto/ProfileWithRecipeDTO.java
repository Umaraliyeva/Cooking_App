package org.example.cooking_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileWithRecipeDTO {
    private ProfileDTO profile;
    private List<RecipeDTO> recipe;
}
