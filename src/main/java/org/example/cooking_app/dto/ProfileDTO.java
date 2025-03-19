package org.example.cooking_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private Integer id;
    private String fullName;
    private Integer profilePictureId; // Profil rasmi faqat id ko‘rinishida
    private String info;
    private Integer followersCount;
    private Integer followingCount;
    private Integer recipesCount;
    private String profession;
}
