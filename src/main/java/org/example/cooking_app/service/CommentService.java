package org.example.cooking_app.service;

import lombok.RequiredArgsConstructor;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

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

        notificationService.createNotification(recipe.getUser(), "Fikr bildirildi: " + text);
        return ResponseEntity.status(200).body("Comment qoshildi: " + text);
    }

    public HttpEntity<?> getRecipeComments(Integer recipeId) {
        List<Object[]> results = commentRepository.findByRecipeIdOrderedByDateDescWithCounts(recipeId);

        List<Map<String, Object>> response = results.stream().map(row -> {
            Comment comment = (Comment) row[0];
            Integer likeCount = (Integer) row[1];
            Integer dislikeCount = (Integer) row[2];

            Map<String, Object> commentData = new HashMap<>();
            commentData.put("id", comment.getId());
            commentData.put("text", comment.getText());
            commentData.put("date", comment.getDate());
            commentData.put("likeCount", likeCount);
            commentData.put("dislikeCount", dislikeCount);
            return commentData;
        }).toList();

        return ResponseEntity.status(200).body(response);
    }

    public HttpEntity<?> clickLikeToComment(User user, Integer commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        if (comment.getLikes() == null) {
            comment.setLikes(new HashMap<>());
        }
        if (comment.getDislikes() == null) {
            comment.setDislikes(new HashMap<>());
        }

        // Agar user allaqachon like bosgan bo‘lsa, like ni olib tashlaymiz
        if (comment.getLikes().containsKey(user)) {
            comment.getLikes().remove(user);
            commentRepository.save(comment);
            return ResponseEntity.status(200).body("Like olib tashlandi");
        }

        // Agar user oldin dislike bosgan bo‘lsa, dislike ni olib tashlaymiz
        if (comment.getDislikes().containsKey(user)) {
            comment.getDislikes().remove(user);
        }

        // Userga like qo‘shish
        comment.getLikes().put(user, 1);
        commentRepository.save(comment);

        if (!comment.getUser().getId().equals(user.getId())) {
            notificationService.createNotification(comment.getUser(), "Sizning commentingizga: " + user.getFullName() + " like bosdi");
        }
        return ResponseEntity.status(200).body("Like bosildi");
    }

    public HttpEntity<?> clickDislikeToComment(User user, Integer commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        if (comment.getDislikes() == null) {
            comment.setDislikes(new HashMap<>());
        }
        if (comment.getLikes() == null) {
            comment.setLikes(new HashMap<>());
        }

        // Agar user allaqachon dislike bosgan bo‘lsa, dislike ni olib tashlaymiz
        if (comment.getDislikes().containsKey(user)) {
            comment.getDislikes().remove(user);
            commentRepository.save(comment);
            return ResponseEntity.status(200).body("Dislike olib tashlandi");
        }

        // Agar user oldin like bosgan bo‘lsa, like ni olib tashlaymiz
        if (comment.getLikes().containsKey(user)) {
            comment.getLikes().remove(user);
        }

        // Userga dislike qo‘shish
        comment.getDislikes().put(user, 1);
        commentRepository.save(comment);
        if (!comment.getUser().getId().equals(user.getId())) {
            notificationService.createNotification(comment.getUser(), "Sizning commentingizga: " + user.getFullName() + " dislike bosdi");
        }
        return ResponseEntity.status(200).body("Dislike bosildi");
    }

}
