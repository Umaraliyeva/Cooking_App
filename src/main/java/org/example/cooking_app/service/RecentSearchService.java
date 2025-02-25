package org.example.cooking_app.service;


import org.example.cooking_app.entity.RecentSearch;
import org.example.cooking_app.entity.Recipe;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.RecentSearchRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RecentSearchService {


    private final RecentSearchRepository recentSearchRepository;

    public RecentSearchService(RecentSearchRepository recentSearchRepository) {
        this.recentSearchRepository = recentSearchRepository;
    }

    public HttpEntity<?> saveRecentSearch(User user, Recipe recipe) {
        RecentSearch recentSearch = RecentSearch.builder()
                .user(user)
                .recipe(recipe)
                .date(LocalDateTime.now()) // Search qilingan vaqt
                .build();
       return ResponseEntity.status(201).body( recentSearchRepository.save(recentSearch));
    }


}
