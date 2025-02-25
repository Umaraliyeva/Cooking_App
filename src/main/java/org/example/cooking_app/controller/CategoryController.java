package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.service.CategoryService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;


    @Tag(name = " hamma category keladi")
    @GetMapping
    public HttpEntity<?>getAllCategories() {
        return ResponseEntity.status(200).body( categoryService.getAllCategories());
    }
}
