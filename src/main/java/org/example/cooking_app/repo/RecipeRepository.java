package org.example.cooking_app.repo;

import org.example.cooking_app.dto.RecipeDTO;
import org.example.cooking_app.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    List<Recipe> findByCategories_Id(Integer categoryId);

    List<Recipe> getRecipeById(Integer id);


    @Query("SELECT r FROM Recipe r WHERE r.createdAt BETWEEN :fromTime AND :toTime")
    List<Recipe> findRecipesInDateRange(@Param("fromTime") LocalDateTime fromTime,
                                        @Param("toTime") LocalDateTime toTime);


    List<Recipe> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    @Query("SELECT r FROM Recipe r " +
            "LEFT JOIN RecentSearch rs ON r.id = rs.recipe.id " +
            "WHERE " +
            "(:fromDate IS NULL OR r.createdAt >= :fromDate) AND " +
            "(:toDate IS NULL OR r.createdAt <= :toDate) AND " +
            "(:popularRecipeIds IS NULL OR r.id IN :popularRecipeIds) AND " +
            "(:rateFilter IS NULL OR r.likes >= :rateFilter) AND " +
            "(:categoryId IS NULL OR :categoryId IN (SELECT c.id FROM r.categories c)) " +
            "GROUP BY r.id " +
            "ORDER BY " +
            "CASE WHEN :timeFilter = 'newest' THEN r.createdAt END DESC, " +
            "CASE WHEN :timeFilter = 'oldest' THEN r.createdAt END ASC, " +
            "CASE WHEN :timeFilter = 'popularity' THEN COUNT(rs.recipe.id) END DESC")
    List<Recipe> filterRecipes(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("popularRecipeIds") List<Integer> popularRecipeIds,
            @Param("rateFilter") Integer rateFilter,
            @Param("categoryId") Integer categoryId,
            @Param("timeFilter") String timeFilter
    );



    //Profile uchun recipe dan kerakli malumotlarni yegib olish
    @Query(value = """
    SELECT r.id, r.name, r.description, r.photo_id AS photoId, r.duration, r.likes, 
           r.link, STRING_AGG(s.steps, ',') AS steps, 
           NULL AS ingredients, NULL AS categories, r.created_at AS createdAt
    FROM recipe r
    LEFT JOIN recipe_steps s ON r.id = s.recipe_id
    WHERE r.user_id = :userId
    GROUP BY r.id, r.name, r.description, r.photo_id, r.duration, r.likes, 
             r.link, r.created_at
""", nativeQuery = true)
    List<Object[]> getRecipesByUserId(@Param("userId") Integer userId);


}