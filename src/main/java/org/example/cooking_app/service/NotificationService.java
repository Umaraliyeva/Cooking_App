package org.example.cooking_app.service;

import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.Notification;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;


    public HttpEntity<?> createNotification(User receiver, String message) {
        Notification notification = new Notification(null, message, false, LocalDateTime.now(), receiver);
        notificationRepository.save(notification);
        simpMessageSendingOperations.convertAndSend("/topic/news", message);
        return ResponseEntity.status(200).body(notification);
    }


    public HttpEntity<?> createNotifications(List<User> receivers, String message) {
        List<Notification> notifications = new ArrayList<>();

        for (User receiver : receivers) {
            Notification notification = new Notification(null, message, false, LocalDateTime.now(), receiver);
            notifications.add(notification);
        }

        notificationRepository.saveAll(notifications);
        simpMessageSendingOperations.convertAndSend("/topic/news", message);

        return ResponseEntity.status(200).body(notifications);
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
