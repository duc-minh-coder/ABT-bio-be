package com.NMCNPM.ABT_bio.enums;

public enum OrderStatusEnum {
    PENDING,    // Mới tạo, chưa thanh toán
    PAID,       // Đã thanh toán, CHỜ Seller giao hàng (Tiền đang giữ ở Admin)
    COMPLETED,  // Seller đã bấm "Xác nhận", tiền đã về ví Seller -> Kết thúc
    CANCELLED,  // Hủy đơn
    REFUNDED    // Hoàn tiền (nếu Seller không giao được)
}
