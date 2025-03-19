package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.NotificationRepository;
import org.example.cooking_app.service.NotificationService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "Foydalanuvchining notificationlarini boshqarish")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Operation(summary = "Get notification", description = "Login qilgan foydalanuvchining barcha notification larini qaytaradi")
    @GetMapping()
    public HttpEntity<?> getNotification(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(notificationService.getAllNotificationsByUserId(user.getId()));
    }

    @Operation(summary = "Get read notification", description = "Login qilgan foydalanuvchining faqat o'qilgan notification larni qaytaradi")
    @GetMapping("/read")
    public HttpEntity<?> getReadNotification(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(notificationService.getReadNotification(user.getId()));
    }

    @Operation(summary = "Get unread notifications", description = "Login qilgan foydalanuvchining faqat o'qilmagan notification larini qaytaradi")
    @GetMapping("/unread")
    public HttpEntity<?> getUnreadNotification(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(notificationService.getUnreadNotifications(user.getId()));
    }

    @Operation(summary = "Notification read = true", description = "Berilgan notificationId bo‘yicha notification 'read = true' qilib belgilaydi")
    @PostMapping("/{notificationId}")
    public HttpEntity<?> readSetTrue(@PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationService.setNotificationRead(notificationId));
    }
}
