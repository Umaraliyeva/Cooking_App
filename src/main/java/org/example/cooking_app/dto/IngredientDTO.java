package org.example.cooking_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientDTO {
    private Integer id;
    private String name;
    private Integer photoId; //AttachmentId ni olib keladi
    private Integer quantity;


}
