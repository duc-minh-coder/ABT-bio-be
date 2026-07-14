package com.NMCNPM.ABT_bio.entity;

import com.NMCNPM.ABT_bio.enums.OrderStatusEnum;
import com.NMCNPM.ABT_bio.enums.ReportStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    // --- MÃ ĐƠN HÀNG (Dễ đọc cho User) ---
    // Ví dụ: #ORD-123456
    @Column(name = "order_code", unique = true, nullable = false)
    String orderCode;

    // --- QUAN HỆ (Người mua - Người bán - Sản phẩm) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @JsonIgnore
    Users buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    Product product;

    // --- QUAN HỆ THANH TOÁN ---
    // Link trực tiếp đến Transaction để biết đơn này trả bằng giao dịch nào
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_transaction_id")
    @JsonIgnore
    PaymentTransactions paymentTransaction;

    @JoinColumn(name = "buyer_content", columnDefinition = "TEXT")
    String buyerContent;

    // --- SNAPSHOT (Lưu cứng thông tin LÚC MUA) ---
    // Để sau này Seller sửa tên/giá sản phẩm thì đơn hàng cũ không bị đổi theo
    @Column(name = "product_name_snapshot", nullable = false)
    String productName;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    Integer quantity = 1;

    // --- TÀI CHÍNH (Tiền nong) ---
    @Column(nullable = false)
    BigDecimal unitPrice; // Giá 1 sản phẩm lúc mua

    @Column(nullable = false)
    BigDecimal totalAmount; // Tổng tiền User phải trả (unitPrice * quantity)

    @Column(nullable = false)
    BigDecimal platformFee; // Phí sàn thu (Ví dụ 5% của totalAmount)

    @Column(nullable = false)
    BigDecimal netAmount; // Tiền thực nhận của Seller (totalAmount - platformFee)

    @Column(nullable = false, length = 10)
    String currency; // VND hoặc USD

    @Column(name = "vat_amount", precision = 18, scale = 2)
    BigDecimal vatAmount;

    // --- MÃ GIẢM GIÁ (COUPON) ---
    @Column(name = "coupon_code", length = 50)
    String couponCode; // Mã giảm giá đã sử dụng (nếu có)

    @Column(name = "discount_amount", precision = 19, scale = 2)
    @Builder.Default
    BigDecimal discountAmount = BigDecimal.ZERO; // Số tiền được giảm

    // --- DELIVERY (Hàng hóa) ---
    // Quan trọng: Chỉ hiển thị cho User khi Status = COMPLETED
    @Column(name = "delivery_content", columnDefinition = "TEXT")
    String deliveryContent; // Link download / Key bản quyền...

    // --- TRẠNG THÁI ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatusEnum status; // PENDING -> PAID -> COMPLETED (hoặc CANCELLED)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    // --- AFFILIATE ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id")
    @JsonIgnore
    Users referrer; // Người giới thiệu (null nếu không qua link affiliate)

    @Column(name = "affiliate_commission", precision = 19, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal affiliateCommission = BigDecimal.ZERO; // Tiền hoa hồng cắt cho Referrer

    @Column(name = "affiliate_percent_snapshot", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal affiliatePercentSnapshot = BigDecimal.ZERO; // % hoa hồng lúc mua (để đối soát)

    @Column(name = "referral_code_used", length = 20)
    String referralCodeUsed; // Mã giới thiệu đã dùng

    // --- KHIẾU NẠI ---
    // Nội dung khiếu nại
    @Column(name = "complaint_content", columnDefinition = "TEXT")
    String complaintContent;

    // Thay boolean bằng Enum
    @Enumerated(EnumType.STRING)
    @Column(name = "complaint_status")
    @Builder.Default
    ReportStatusEnum complaintStatus = ReportStatusEnum.PENDING; // Mặc định là PENDING

    @Column(name = "is_report_read")
    Boolean isReportRead;

    // Cờ xóa mềm
    @Column(name = "is_complaint_deleted")
    @Builder.Default
    Boolean isComplaintDeleted = false;

    @Column(name = "message", columnDefinition = "TEXT")
    String message;
}
