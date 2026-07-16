package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.response.CategoryResponse;
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
    public ApiResponse<List<CategoryResponse>> list(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        var p = categoryService.listPaged(PageRequest.of(page, size));
        List<CategoryResponse> categories = p.getContent().stream().map(this::toCategoryResponse).toList();
        return ApiResponse.<List<CategoryResponse>>builder().code(0).result(categories).build();
    }

    @PostMapping("/admin/categories")
    public ApiResponse<CategoryResponse> create(@RequestBody Category c) {
        Category saved = categoryService.create(c);
        return ApiResponse.<CategoryResponse>builder().code(0).result(toCategoryResponse(saved)).build();
    }

    @PutMapping("/admin/categories/{id}")
    public ApiResponse<CategoryResponse> update(@PathVariable Long id, @RequestBody Category c) {
        Category updated = categoryService.update(id, c);
        if (updated == null) return ApiResponse.<CategoryResponse>builder().code(1).message("Not found").build();
        return ApiResponse.<CategoryResponse>builder().code(0).result(toCategoryResponse(updated)).build();
    }

    @DeleteMapping("/admin/categories/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder().code(0).message("Deleted").build();
    }

    private CategoryResponse toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .image(category.getImage())
                .description(category.getDescription())
                .status(category.getStatus())
                .productCount(category.getProductCount())
                .build();
    }
}
