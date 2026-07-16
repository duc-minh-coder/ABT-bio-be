package com.NMCNPM.ABT_bio.controller;

import com.NMCNPM.ABT_bio.dto.response.ProductResponse;
import com.NMCNPM.ABT_bio.entity.Product;
import com.NMCNPM.ABT_bio.dto.request.CreateProductRequest;
import com.NMCNPM.ABT_bio.service.ApiContractMapper;
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
    private final ApiContractMapper apiContractMapper;

    @GetMapping("/products")
    public ApiResponse<List<ProductResponse>> listProducts(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var p = productService.list(keyword, categoryId, PageRequest.of(page, size));
        List<ProductResponse> products = p.getContent().stream().map(apiContractMapper::toProductResponse).toList();
        return ApiResponse.<List<ProductResponse>>builder().code(0).result(products).build();
    }

    @GetMapping("/products/{id}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long id) {
        var p = productService.get(id);
        if (p == null) return ApiResponse.<ProductResponse>builder().code(1).message("Not found").build();
        return ApiResponse.<ProductResponse>builder().code(0).result(apiContractMapper.toProductResponse(p)).build();
    }

    @GetMapping("/products/best-selling")
    public ApiResponse<List<ProductResponse>> bestSelling() {
        List<ProductResponse> products = productService.bestSelling().stream().map(apiContractMapper::toProductResponse).toList();
        return ApiResponse.<List<ProductResponse>>builder().code(0).result(products).build();
    }

    @PostMapping("/admin/products")
    public ApiResponse<ProductResponse> createProduct(@RequestBody CreateProductRequest req) {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        Product product = productService.create(req, principal);
        return ApiResponse.<ProductResponse>builder().code(0).result(apiContractMapper.toProductResponse(product)).build();
    }

    @PutMapping("/admin/products/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody CreateProductRequest req) {
        Product product = productService.update(id, req);
        if (product == null) return ApiResponse.<ProductResponse>builder().code(1).message("Not found").build();
        return ApiResponse.<ProductResponse>builder().code(0).result(apiContractMapper.toProductResponse(product)).build();
    }

    @DeleteMapping("/admin/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.<Void>builder().code(0).message("Deleted").build();
    }

}
