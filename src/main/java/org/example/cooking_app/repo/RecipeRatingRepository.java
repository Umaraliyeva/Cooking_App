package org.example.cooking_app.repo;

import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.RecipeRating;
import org.example.cooking_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRatingRepository extends JpaRepository<RecipeRating, Integer> {

  RecipeRating findByRecipeAndUser(Recipe recipe, User user);

  List<RecipeRating> findByRecipe(Recipe recipe);
}