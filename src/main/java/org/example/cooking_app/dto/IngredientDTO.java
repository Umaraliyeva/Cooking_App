package org.example.cooking_app.dto;

import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cooking_app.entity.Attachment;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientDTO {
    private Integer id;
    private String name;
    private Integer photoId; //AttachmentId ni olib keladi
    private Integer quantity;
}
