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
   private Attachment attachment;
   private Integer quantity;

    public IngredientDTO(Attachment attachment, String name, Integer quantity) {
       this.name = name;
       this.attachment = attachment;
       this.quantity = quantity;
   }


}
