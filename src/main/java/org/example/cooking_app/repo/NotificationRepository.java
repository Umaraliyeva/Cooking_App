package org.example.cooking_app.repo;

import org.example.cooking_app.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    //Login qilgan userga tegishli notificationlarni qaytaradi
    @Query("""
    SELECT n.id, n.title, n.read, n.dateTime 
    FROM Notification n 
    WHERE n.user.id = :id 
    ORDER BY n.dateTime DESC
""")
    List<Object[]> findByUserId(@Param("id") Integer id);


    // Faqat read = true bo'lgan notificationlarni olib keladi (o'qilganlar)
    @Query("""
    SELECT n.id, n.title, n.read, n.dateTime 
    FROM Notification n
    WHERE n.read = true AND n.user.id = :userId
    ORDER BY n.dateTime DESC
""")
    List<Object[]> findReadNotificationsByUser(Integer userId);

    // Unread, ya'ni read = false bo'lgan notificationlarni olib keladi (o'qilmaganlar)
    @Query("""
    SELECT n.id, n.title, n.read, n.dateTime 
    FROM Notification n
    WHERE n.read = false AND n.user.id = :userId
    ORDER BY n.dateTime DESC
""")
    List<Object[]> findUnreadNotificationsByUser(Integer userId);



}