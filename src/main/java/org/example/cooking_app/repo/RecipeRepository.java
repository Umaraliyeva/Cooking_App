package org.example.cooking_app.repo;

import org.example.cooking_app.dto.RecipeDTO;
import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {

    List<Recipe> findByCategories_Id(Integer categoryId);

    List<Recipe> getRecipeById(Integer id);


    @Query("SELECT r FROM Recipe r WHERE r.createdAt BETWEEN :fromTime AND :toTime")
    List<Recipe> findRecipesInDateRange(@Param("fromTime") LocalDateTime fromTime,
                                        @Param("toTime") LocalDateTime toTime);


    List<Recipe> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    @Query(value = "SELECT r.* FROM recipe r " +
            "LEFT JOIN recent_search rs ON r.id = rs.recipe_id " +
            "WHERE " +
            "(:fromDate IS NULL OR r.created_at >= :fromDate) AND " +
            "(:toDate IS NULL OR r.created_at <= :toDate) AND " +
            "(:popularRecipeIds IS NULL OR r.id = ANY(:popularRecipeIds::int[])) AND " +
            "(:rateFilter IS NULL OR r.rating >= :rateFilter) AND " +
            "(:categoryId IS NULL OR EXISTS (SELECT 1 FROM recipe_categories rc WHERE rc.recipe_id = r.id AND rc.categories_id = :categoryId)) " +
            "GROUP BY r.id, r.created_at, r.rating " +
            "ORDER BY " +
            "COALESCE(CASE WHEN :timeFilter = 'newest' THEN r.created_at END, '1970-01-01') DESC, " +
            "COALESCE(CASE WHEN :timeFilter = 'oldest' THEN r.created_at END, '1970-01-01') ASC, " +
            "COALESCE(CASE WHEN :timeFilter = 'popularity' THEN COUNT(rs.recipe_id) END, 0) DESC, " +
            "r.created_at DESC",
            nativeQuery = true)
    List<Recipe> filterRecipes(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("popularRecipeIds") List<Integer> popularRecipeIds,
            @Param("rateFilter") Integer rateFilter,
            @Param("categoryId") Integer categoryId,
            @Param("timeFilter") String timeFilter
    );

    Optional<Recipe> findByNameIgnoreCase(String name);




    //Profile uchun recipe dan kerakli malumotlarni yegib olish
    @Query(value = """
    SELECT r.id, r.name, r.description, r.photo_id AS photoId, r.duration, r.rating, 
           r.link, STRING_AGG(s.steps, ',') AS steps, 
           r.created_at AS createdAt  
    FROM recipe r
    LEFT JOIN recipe_steps s ON r.id = s.recipe_id
    WHERE r.user_id = :userId
    GROUP BY r.id, r.name, r.description, r.photo_id, r.duration, r.rating, 
             r.link, r.created_at
    ORDER BY r.created_at DESC
""", nativeQuery = true)
    List<Object[]> getRecipesByUserId(@Param("userId") Integer userId);




}