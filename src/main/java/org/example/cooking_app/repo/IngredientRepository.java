package org.example.cooking_app.repo;

import org.example.cooking_app.dto.IngredientDTO;
import org.example.cooking_app.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.util.List;
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    Optional<Ingredient> findByName(String name);



    //Profile uchun kerakli qisimni olib kelish
    @Query(value = """
    SELECT i.id, i.name, i.photo_id AS photoId, i.quantity
    FROM ingredient i
    WHERE i.recipe_id = :recipeId
""", nativeQuery = true)
    List<Object[]> getIngredientsByRecipeId(@Param("recipeId") Integer recipeId);

}