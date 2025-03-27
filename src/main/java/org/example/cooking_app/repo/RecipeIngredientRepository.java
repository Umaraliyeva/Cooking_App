package org.example.cooking_app.repo;

import org.example.cooking_app.entity.Ingredient;
import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Integer> {

    void deleteByRecipe(Recipe recipe);
}