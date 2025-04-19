package com.example.myorder.controller;

import com.example.myorder.dto.CategoryDTO;
import com.example.myorder.model.Category;
import com.example.myorder.service.CategoryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<Category>> getAllCategories(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(categoryService.getAllCategories(page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategoriesWithAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDTO category) {
        System.out.println("category"+ category);
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id, 
            @RequestBody CategoryDTO categorydto) {
        Category category = categoryService.getCategory(id);
        category.setName(categorydto.getName());
        category.setDescription(categorydto.getDescription());
        category.setAvailable(categorydto.getAvailable());
        category.setUpdateTime(categorydto.getUpdateTime());
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<Category> disableCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.disableCategory(id));
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<Category> enableCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.enableCategory(id));
    }
} 