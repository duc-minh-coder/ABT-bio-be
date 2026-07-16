package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.dto.request.CreateProductRequest;
import com.NMCNPM.ABT_bio.entity.Category;
import com.NMCNPM.ABT_bio.entity.Product;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.repository.CategoryRepository;
import com.NMCNPM.ABT_bio.repository.ProductRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.NMCNPM.ABT_bio.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Product> list(String keyword, Long categoryId, Pageable pageable) {
        return productRepository.searchProducts(keyword, categoryId, pageable);
    }

    @Override
    public Product get(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> bestSelling() {
        return productRepository.findTop10ByOrderBySoldCountDesc();
    }

    @Override
    public Product create(CreateProductRequest req, String sellerEmail) {
        Product.ProductPrice price = Product.ProductPrice.builder()
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .originalAmount(req.getOriginalAmount())
                .build();

        Product product = Product.builder()
                .name(req.getName())
                .slug(req.getSlug())
                .detailedDescription(req.getDetailedDescription())
                .thumbnailUrl(req.getThumbnailUrl())
                .galleryUrls(req.getGalleryUrls())
                .prices(List.of(price))
                .inventoryCount(req.getInventoryCount())
                .supportEmail(req.getSupportEmail())
                .supportTelegram(req.getSupportTelegram())
                .build();

        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        if (sellerEmail != null) {
            userRepository.findByContactEmail(sellerEmail).ifPresent(product::setSeller);
        }

        return productRepository.save(product);
    }

    @Override
    public Product update(Long id, CreateProductRequest req) {
        var opt = productRepository.findById(id);
        if (opt.isEmpty()) return null;
        Product product = opt.get();
        product.setName(req.getName());
        product.setSlug(req.getSlug());
        product.setDetailedDescription(req.getDetailedDescription());
        product.setThumbnailUrl(req.getThumbnailUrl());
        product.setGalleryUrls(req.getGalleryUrls());
        product.setInventoryCount(req.getInventoryCount());
        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId()).orElse(null);
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
