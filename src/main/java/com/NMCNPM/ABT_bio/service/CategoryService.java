package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    List<Category> list();
    Page<Category> listPaged(Pageable pageable);
    Category create(Category c);
    Category update(Long id, Category c);
    void delete(Long id);
}
