package com.NMCNPM.ABT_bio.repository;

import com.NMCNPM.ABT_bio.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);

    Boolean existsBySlug(String slug);
}
