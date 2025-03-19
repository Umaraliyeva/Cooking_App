package org.example.cooking_app.repo;

import org.example.cooking_app.dto.ProfileDTO;
import org.example.cooking_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByUsername(String username);


  //Profile uchun userdan kerakli malumotlarni yegib olish
  @Query(value = """
   SELECT u.id, u.username, u.full_name AS fullName,
        u.profile_picture_id AS profilePictureId, u.info,
        (SELECT COUNT(*) FROM users_followers uf WHERE uf.followers_id = u.id) AS followersCount,
        (SELECT COUNT(*) FROM users_followings uf WHERE uf.followings_id = u.id) AS followingCount,
        u.profession
   FROM users u WHERE u.id = :userId
""", nativeQuery = true)
  List<Object[]> getProfileDTOById(@Param("userId") Integer userId);






}