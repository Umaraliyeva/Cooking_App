package org.example.cooking_app.service;

import lombok.RequiredArgsConstructor;
import org.example.cooking_app.dto.CommentDTO;
import org.example.cooking_app.entity.Comment;
import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.CommentRepository;
import org.example.cooking_app.repo.RecipeRepository;
import org.example.cooking_app.repo.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;

    public HttpEntity<?> createCommit(String text, User user, Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();

        Comment comment = new Comment(
                null,
                text,
                LocalDateTime.now(),
                user,
                recipe,
                null,
                null);

        commentRepository.save(comment);
        return ResponseEntity.status(200).body(comment);
    }

    public HttpEntity<?> getRecipeComments(Integer recipeId) {
        List<Comment> byRecipeId = commentRepository.findByRecipe_Id(recipeId);
        return ResponseEntity.status(200).body(byRecipeId);
    }
}
