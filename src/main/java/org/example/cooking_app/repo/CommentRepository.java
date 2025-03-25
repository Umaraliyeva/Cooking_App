package org.example.cooking_app.repo;

import org.example.cooking_app.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("""
    SELECT c, 
           COALESCE(SIZE(c.likes), 0) AS likeCount, 
           COALESCE(SIZE(c.dislikes), 0) AS dislikeCount 
    FROM Comment c
    WHERE c.recipe.id = :recipeId
    ORDER BY c.date DESC
""")
    List<Object[]> findByRecipeIdOrderedByDateDescWithCounts(@Param("recipeId") Integer recipeId);


}