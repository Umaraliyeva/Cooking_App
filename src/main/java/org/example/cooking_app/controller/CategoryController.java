package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.Category;
import org.example.cooking_app.repo.CategoryRepository;
import org.example.cooking_app.service.CategoryService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;


    @Tag(name = " hamma category keladi")
    @GetMapping
    public HttpEntity<?>getAllCategories() {
        return ResponseEntity.status(200).body( categoryService.getAllCategories());
    }


    @Tag(name = "category add qilish")
    @PostMapping
    public HttpEntity<?> addCategory(@RequestParam("name") String name) {
        return ResponseEntity.status(201).body(categoryService.saveCategory(name));
    }
}
