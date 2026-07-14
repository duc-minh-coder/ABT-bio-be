package com.NMCNPM.ABT_bio.entity;

import com.NMCNPM.ABT_bio.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "products",
        indexes = {
        @Index(name = "idx_product_slug", columnList = "slug"),
        @Index(name = "idx_product_seller", columnList = "seller_id"),
        @Index(name = "idx_product_category", columnList = "category_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // --- ĐỊNH DANH ---
    @Column(length = 150)
    String name;

    @Column(length = 150)
    String slug;

    @Column(columnDefinition = "TEXT")
    String detailedDescription;

    // --- MEDIA ---
    String thumbnailUrl;

    @ElementCollection
    @CollectionTable(name = "product_gallery_urls", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "url")
    @Builder.Default
    List<String> galleryUrls = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "product_prices",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    List<ProductPrice> prices = new ArrayList<>();

    // --- THỐNG KÊ (Dùng để sort & hiển thị) ---
    @Builder.Default
    Long soldCount = 0L; // Số lượng đã bán

    @Column(name = "is_popular")
    @Builder.Default
    Boolean isPopular = false;

    @Builder.Default
    Integer inventoryCount = 0;

    @Column(name = "is_deleted")
    Boolean deleted = false;

    // --- QUAN HỆ ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @ToString.Exclude
    @JsonIgnore
    Users seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    @JsonIgnore
    Category category;

    // --- TRẠNG THÁI ---
    // Visibility: Ẩn/Hiện do Seller quyết định (Ví dụ: hết hàng tạm ẩn)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    Visibility visibility = Visibility.HIDDEN;

    // Status: Trạng thái kiểm duyệt hệ thống (Do Admin/System quyết định)
    // Ví dụ: BANNED (Vi phạm), ACTIVE (Được phép bán), PENDING (Chờ duyệt)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    ProductStatus status = ProductStatus.ACTIVE;

    String supportEmail;
    String supportTelegram;

    @CreationTimestamp
    @Column(updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    Instant updatedAt;

    // DANH SÁCH ĐƠN HÀNG CỦA SẢN PHẨM
//    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
//    @JsonIgnore
//    @Builder.Default
//    List<Orders> orders = new ArrayList<>();


    // inner class
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProductPrice {

        @Column(nullable = false)
        BigDecimal amount; // Giá bán

        BigDecimal originalAmount; // Giá gốc (để hiện giảm giá)

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        Currency currency; // Loại tiền: VND, USD, EUR...
    }
}
