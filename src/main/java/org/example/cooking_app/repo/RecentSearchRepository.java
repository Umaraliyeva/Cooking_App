package org.example.cooking_app.repo;

import org.example.cooking_app.entity.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Integer> {
}