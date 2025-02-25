package org.example.cooking_app.service;

import org.example.cooking_app.entity.Category;
import org.example.cooking_app.repo.CategoryRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public HttpEntity<?> getAllCategories() {
        return ResponseEntity.ok( categoryRepository.findAll());
    }

}
