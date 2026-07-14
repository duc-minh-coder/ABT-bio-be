package com.NMCNPM.ABT_bio.entity;

import com.NMCNPM.ABT_bio.enums.CategoryStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 100)
    String name;

    // Slug cho category để làm URL đẹp (VD: /danh-muc/tools)
    @Column(nullable = false, unique = true, length = 100)
    String slug;

    @Column(name = "image", columnDefinition = "TEXT")
    String image;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "status", nullable = false)
    CategoryStatusEnum status;

    /**
     * Đây là cột "ảo" (Read-only).
     * Hibernate sẽ chạy câu SQL trong ngoặc để tính toán giá trị này mỗi khi load Category.
     * Lợi ích: Luôn đúng, không cần code logic update số lượng thủ công.
     */
    @Formula("(SELECT COUNT(*) FROM products p WHERE p.category_id = id)")
    Long productCount;

    // product
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @ToString.Exclude // Tránh vòng lặp vô tận khi in log
    List<Product> products = new ArrayList<>();
}
