package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> list();
    Category create(Category c);
    Category update(Long id, Category c);
    void delete(Long id);
}
