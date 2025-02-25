package org.example.cooking_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    private LocalDateTime date;
    @ManyToOne
    private User user;
    @ManyToOne
    private Recipe recipe;
    @ElementCollection
    private Map<User,Integer> likes;
    @ElementCollection
    private Map<User,Integer> dislikes;
}
