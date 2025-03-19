package org.example.cooking_app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private Integer id;
    private String username;
    private String fullName;
    private Integer profilePictureId; // Profil rasmi faqat id ko‘rinishida
    private String info;
    private Integer followersCount;
    private Integer followingCount;
    private String profession;
    //private List<RecipeDTO> recipes; // Receptlar alohida DTO ko‘rinishida

}
