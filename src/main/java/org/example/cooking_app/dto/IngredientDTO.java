package org.example.cooking_app.dto;

import lombok.Data;
import org.example.cooking_app.entity.Attachment;

@Data
public class IngredientDTO {
   private String name;
   private Attachment attachment;
   private Integer quantity;

    public IngredientDTO(Attachment attachment, String name, Integer quantity) {
       this.name = name;
       this.attachment = attachment;
       this.quantity = quantity;
   }

}
