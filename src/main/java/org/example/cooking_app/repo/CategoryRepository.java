package org.example.cooking_app.repo;

import org.example.cooking_app.dto.CategoryDTO;
import org.example.cooking_app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    //Profile uchun kerakli qisimni yegib kelish
    @Query(value = """
    SELECT c.id, c.name 
    FROM category c
    JOIN recipe_categories rc ON c.id = rc.categories_id
    WHERE rc.recipe_id = :recipeId
""", nativeQuery = true)
    List<Object[]> getCategoriesByRecipeId(@Param("recipeId") Integer recipeId);

}