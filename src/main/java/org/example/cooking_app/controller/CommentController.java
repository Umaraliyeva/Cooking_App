package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.dto.CommentDTO;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.service.CommentService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commit")
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "Kommentariyalar bo‘yicha amallar")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Get comments", description = "Berilgan recipeId bo‘yicha barcha kommentlarni qaytaradi")
    @GetMapping("/{recipeId}")
    public HttpEntity<?> getComment(@PathVariable Integer recipeId) {
        return ResponseEntity.status(200).body(commentService.getRecipeComments(recipeId));
    }

    @Operation(summary = "Create comment", description = "Foydalanuvchi va recipe ID bo‘yicha yangi komment yaratadi")
    @PostMapping("/create/{recipeId}")
    public HttpEntity<?> createComment(@RequestBody CommentDTO commentDTO,
                                       @AuthenticationPrincipal User user,
                                       @PathVariable Integer recipeId) {
        return ResponseEntity.status(200).body(commentService.createCommit(commentDTO.getText(), user, recipeId));
    }

    @Operation(summary = "Click like", description = "Commentga like bosish")
    @PostMapping("/clickLike/{commentId}")
    public HttpEntity<?> clickLike(@AuthenticationPrincipal User user,
                                   @PathVariable Integer commentId) {
        return ResponseEntity.status(200).body(commentService.clickLikeToComment(user, commentId));
    }

    @Operation(summary = "Click dislike", description = "Commentga dislike bosish")
    @PostMapping("/clickDislike/{commentId}")
    public HttpEntity<?> clickDislike(@AuthenticationPrincipal User user,
                                   @PathVariable Integer commentId) {
        return ResponseEntity.status(200).body(commentService.clickDislikeToComment(user, commentId));
    }

}
