package org.example.cooking_app.service;

import org.example.cooking_app.entity.Notification;
import org.example.cooking_app.repo.NotificationRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public HttpEntity<?> getAllNotificationsByUserId(Integer id) {
        return ResponseEntity.ok(notificationRepository.findByUser_Id(id));
    }

    public HttpEntity<?> getReadNotification(Integer id) {
        return ResponseEntity.ok(notificationRepository.findReadNotificationsByUser(id));
    }

    public HttpEntity<?> getUnreadNotifications(Integer id) {
        return ResponseEntity.ok(notificationRepository.findUnreadNotificationsByUser(id));
    }

    public String setNotificationRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification topilmadi"));

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
            return "Notification read = true qilindi";
        }
        return "Notification allaqachon read = true";
    }
}
