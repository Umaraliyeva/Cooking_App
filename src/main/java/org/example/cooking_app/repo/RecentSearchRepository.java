package org.example.cooking_app.repo;

import org.example.cooking_app.entity.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Integer> {

    @Query("SELECT rs.recipe.id FROM RecentSearch rs " +
            "GROUP BY rs.recipe.id " +
            "ORDER BY COUNT(rs.recipe.id) DESC")
    List<Integer> findMostSearchedRecipeIds();
}