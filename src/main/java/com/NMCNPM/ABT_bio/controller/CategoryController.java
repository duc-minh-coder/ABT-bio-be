package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.NMCNPM.ABT_bio.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    private final com.NMCNPM.ABT_bio.service.CategoryService categoryService;

    @GetMapping("/categories")
    public ApiResponse<List<Category>> list(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        var p = categoryService.listPaged(PageRequest.of(page, size));
        return ApiResponse.<List<Category>>builder().code(0).result(p.getContent()).build();
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
