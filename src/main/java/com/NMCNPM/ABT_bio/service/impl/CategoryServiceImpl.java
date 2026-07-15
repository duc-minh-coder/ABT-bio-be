package com.NMCNPM.ABT_bio.service.impl;

import com.NMCNPM.ABT_bio.entity.Category;
import com.NMCNPM.ABT_bio.repository.CategoryRepository;
import com.NMCNPM.ABT_bio.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> list() {
        return categoryRepository.findAll();
    }

    @Override
    public Category create(Category c) {
        return categoryRepository.save(c);
    }

    @Override
    public Category update(Long id, Category c) {
        return categoryRepository.findById(id).map(existing -> {
            existing.setName(c.getName());
            existing.setSlug(c.getSlug());
            existing.setImage(c.getImage());
            existing.setDescription(c.getDescription());
            existing.setStatus(c.getStatus());
            return categoryRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
