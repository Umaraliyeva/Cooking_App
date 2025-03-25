package org.example.cooking_app.repo;

import org.example.cooking_app.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    Optional<Ingredient> findByName(String name);


}