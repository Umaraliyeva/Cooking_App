package org.example.cooking_app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    @ManyToOne
    private Attachment photo;
    private Integer duration;

    @Min(value = 1) @Max(value = 5)
    private Integer likes;
    private String link;
    @ElementCollection
    private List<String> steps;

    @ManyToOne // ✅ Ko‘plab Recipe-lar bitta User ga tegishli bo‘ladi
    private User user;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Ingredient> ingredients;

    @ManyToMany
    List<Category> categories;
    @CreationTimestamp
    private LocalDateTime createdAt; // vaqt berish kere qachon create qilinganiga qarab
                                    // recently added da sort qilib chiqaramiz
                                   //  masalan 2 kun oldin qo'shilgan yangi receptlarni chiqaramiz

}
