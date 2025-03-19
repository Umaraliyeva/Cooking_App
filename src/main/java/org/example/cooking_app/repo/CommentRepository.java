package org.example.cooking_app.repo;

import org.example.cooking_app.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByRecipe_Id(Integer recipeId);
}