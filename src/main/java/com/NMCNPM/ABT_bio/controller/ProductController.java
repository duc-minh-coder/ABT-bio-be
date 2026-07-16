package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.entity.Product;
import com.NMCNPM.ABT_bio.dto.request.CreateProductRequest;
import com.NMCNPM.ABT_bio.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.NMCNPM.ABT_bio.dto.ApiResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public ApiResponse<List<Product>> listProducts(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var p = productService.list(keyword, categoryId, PageRequest.of(page, size));
        return ApiResponse.<List<Product>>builder().code(0).result(p.getContent()).build();
    }

    @GetMapping("/products/{id}")
    public ApiResponse<Product> getProduct(@PathVariable Long id) {
        var p = productService.get(id);
        if (p == null) return ApiResponse.<Product>builder().code(1).message("Not found").build();
        return ApiResponse.<Product>builder().code(0).result(p).build();
    }

    @GetMapping("/products/best-selling")
    public ApiResponse<List<Product>> bestSelling() {
        return ApiResponse.<List<Product>>builder().code(0).result(productService.bestSelling()).build();
    }

    @PostMapping("/admin/products")
    public ApiResponse<Product> createProduct(@RequestBody CreateProductRequest req) {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        Product product = productService.create(req, principal);
        return ApiResponse.<Product>builder().code(0).result(product).build();
    }

    @PutMapping("/admin/products/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable Long id, @RequestBody CreateProductRequest req) {
        Product product = productService.update(id, req);
        if (product == null) return ApiResponse.<Product>builder().code(1).message("Not found").build();
        return ApiResponse.<Product>builder().code(0).result(product).build();
    }

    @DeleteMapping("/admin/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.<Void>builder().code(0).message("Deleted").build();
    }

}
