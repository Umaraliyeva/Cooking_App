package org.example.cooking_app.service;

import jakarta.persistence.criteria.Join;
import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.repo.RecentSearchRepository;
import org.example.cooking_app.repo.RecipeRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecentSearchRepository recentSearchRepository;

    public RecipeService(RecipeRepository recipeRepository, RecentSearchRepository recentSearchRepository) {
        this.recipeRepository = recipeRepository;
        this.recentSearchRepository = recentSearchRepository;
    }

    public HttpEntity<?> getRecipesByCategory(Integer categoryId) {
        List<Recipe> recipes = recipeRepository.findByCategories_Id(categoryId);
        return ResponseEntity.ok(recipes);
    }

    public HttpEntity<?> getRecipesFromLastTwoDays() {
        // 23-fevralning 00:00:00 dan bugungi kunga qadar
        LocalDateTime fromTime = LocalDate.now().minusDays(2).atStartOfDay(); // 23-fevral 00:00:00
        LocalDateTime toTime = LocalDateTime.now(); // bugun

        List<Recipe> recipes = recipeRepository.findRecipesInDateRange(fromTime, toTime);
        return ResponseEntity.ok(recipes);
    }

    public HttpEntity<?> getRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return ResponseEntity.ok(recipes);
    }

    public List<Recipe> searchRecipes(String search) {
        return  recipeRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
    }

    public List<Recipe> filterRecipes(String timeFilter, Integer rateFilter, Integer categoryId) {
        Specification<Recipe> spec = Specification.where(null);

        // 📌 POPULARITY bo‘yicha saralash
        if ("popularity".equalsIgnoreCase(timeFilter)) {
            List<Integer> popularRecipeIds = recentSearchRepository.findMostSearchedRecipeIds();
            if (!popularRecipeIds.isEmpty()) {
                spec = spec.and((root, query, criteriaBuilder) -> root.get("id").in(popularRecipeIds));
            }
        }

        // ⭐ RATE FILTER (likes >= rateFilter)
        if (rateFilter != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("likes"), rateFilter));
        }

        // 📂 CATEGORY FILTER
        if (categoryId != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Object, Object> categoryJoin = root.join("categories");
                return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
            });
        }

        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt"));
        return recipeRepository.findAll(spec, pageable).getContent();
    }


    private org.springframework.data.domain.Sort getSorting(String timeFilter) {
        if ("newest".equalsIgnoreCase(timeFilter)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        } else if ("oldest".equalsIgnoreCase(timeFilter)) {
            return Sort.by(Sort.Direction.ASC, "createdAt");
        }
        return Sort.by(Sort.Direction.DESC, "createdAt");// Popularity sorting yuqorida hal qilindi
    }


    public Recipe getRecipeById(Integer recipeId) {
        return recipeRepository.findById(recipeId).orElse(null);
    }
}
