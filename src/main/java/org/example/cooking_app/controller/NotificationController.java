package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.NotificationRepository;
import org.example.cooking_app.service.NotificationService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor

public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping()
    @Tag(name = "Login qilgan userni notificationlarini olib keladi")
    public HttpEntity<?> getNotification(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(notificationService.getAllNotificationsByUserId(user.getId()));
    }

    @GetMapping("/read")
    @Tag(name = "Login qilgan userni oqilgan notificationlarini olib keladi")
    public HttpEntity<?> getReadNotification(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(notificationService.getReadNotification(user.getId()));
    }

    @GetMapping("/unread")
    @Tag(name = "Login qilgan userni oqilmagan notificationlarini olib keladi")
    public HttpEntity<?> getUnreadNotification(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(notificationService.getUnreadNotifications(user.getId()));
    }

    @PostMapping("/{notificationId}")
    @Tag(name = "Notificationni read = true qilish")
    public HttpEntity<?> readSetTrue(@PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationService.setNotificationRead(notificationId));
    }

}
