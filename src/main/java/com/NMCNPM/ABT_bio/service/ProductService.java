package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.dto.request.CreateProductRequest;
import com.NMCNPM.ABT_bio.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<Product> list(String keyword, Long categoryId, Pageable pageable);
    Product get(Long id);
    List<Product> bestSelling();
    Product create(CreateProductRequest req, String sellerEmail);
    Product update(Long id, CreateProductRequest req);
    void delete(Long id);
}
