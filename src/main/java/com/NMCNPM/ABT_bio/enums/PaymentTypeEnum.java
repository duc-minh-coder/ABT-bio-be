package com.NMCNPM.ABT_bio.enums;

import lombok.Getter;

@Getter
public enum PaymentTypeEnum {
    DEPOSIT,       // Nạp tiền vào ví
    ORDER_PAYMENT,  // Thanh toán trực tiếp cho đơn hàng
    WITHDRAWAL
}
