package org.example.cooking_app.service;

import org.example.cooking_app.entity.Notification;
import org.example.cooking_app.repo.NotificationRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Map<String, Object>> getAllNotificationsByUserId(Integer id) {
        List<Object[]> results = notificationRepository.findByUserId(id);

        return results.stream().map(obj -> Map.of(
                "id", obj[0],
                "title", obj[1],
                "read", obj[2],
                "dateTime", obj[3]
        )).toList();
    }


    public List<Map<String, Object>> getReadNotification(Integer userId) {
        List<Object[]> notifications = notificationRepository.findReadNotificationsByUser(userId);

        return notifications.stream().map(n -> Map.of(
                "id", n[0],
                "title", n[1],
                "read", n[2],
                "dateTime", n[3]
        )).toList();
    }


    public List<Map<String, Object>> getUnreadNotifications(Integer userId) {
        List<Object[]> notifications = notificationRepository.findUnreadNotificationsByUser(userId);

        return notifications.stream().map(n -> Map.of(
                "id", n[0],
                "title", n[1],
                "read", n[2],
                "dateTime", n[3]
        )).toList();
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
