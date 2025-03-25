package org.example.cooking_app.repo;

import org.example.cooking_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByUsername(String username);


  @Query(nativeQuery = true,value = """
            select exists(select *
            from recipe r
                     inner join users_followers uf on r.user_id=uf.user_id
                        and r.id=:recipeId
                        and uf.followers_id=:userId)
          """)
  Boolean isFollower(int userId, int recipeId);

  @Query("SELECT f FROM User u JOIN u.followers f WHERE u.id = :userId")
  List<User> getFollowersByUserId(@Param("userId") Integer userId);

}