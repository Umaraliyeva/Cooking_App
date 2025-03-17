package org.example.cooking_app.repo;

import org.example.cooking_app.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    //Login qilgan userga tegishli notificationlarni qaytaradi
    List<Notification> findByUser_Id(Integer userId);

    //Faqat read = true larni olib keladigan query
    @Query(value = """
        SELECT * FROM notification n
        JOIN users u ON n.user_id = u.id
        WHERE n.read = true AND u.id = :userId
        
""", nativeQuery = true)
    List<Notification> findReadNotificationsByUser(Integer userId);


    //Unread yaniy read = false bolganlarini olib keladi
    @Query(value = """
    SELECT * FROM notification n
    JOIN users u ON n.user_id = u.id
    WHERE n.read = false AND u.id = :userId
""", nativeQuery = true)
    List<Notification> findUnreadNotificationsByUser(Integer userId);

}