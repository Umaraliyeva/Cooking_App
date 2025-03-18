package org.example.cooking_app.service;

import org.example.cooking_app.entity.Category;
import org.example.cooking_app.repo.CategoryRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public HttpEntity<?> getAllCategories() {
        return ResponseEntity.ok( categoryRepository.findAll());
    }

    public HttpEntity<?> saveCategory(String name) {
        Optional<Category> optionalCategory = categoryRepository.findByName(name);
        if (optionalCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Bu nomdagi category allaqachon mavjud!");
        }else {
            return ResponseEntity.status(201).body(categoryRepository.save(Category.builder().name(name).build()));
        }
    }
}
