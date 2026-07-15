package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.entity.Category;
import com.NMCNPM.ABT_bio.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.NMCNPM.ABT_bio.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    private final com.NMCNPM.ABT_bio.service.CategoryService categoryService;

    @GetMapping("/categories")
    public ApiResponse<List<Category>> list() {
        return ApiResponse.<List<Category>>builder().code(0).result(categoryService.list()).build();
    }

    @PostMapping("/admin/categories")
    public ApiResponse<Category> create(@RequestBody Category c) {
        Category saved = categoryService.create(c);
        return ApiResponse.<Category>builder().code(0).result(saved).build();
    }

    @PutMapping("/admin/categories/{id}")
    public ApiResponse<Category> update(@PathVariable Long id, @RequestBody Category c) {
        Category updated = categoryService.update(id, c);
        if (updated == null) return ApiResponse.<Category>builder().code(1).message("Not found").build();
        return ApiResponse.<Category>builder().code(0).result(updated).build();
    }

    @DeleteMapping("/admin/categories/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder().code(0).message("Deleted").build();
    }
}
