package com.NMCNPM.ABT_bio.enums;

import lombok.Getter;

@Getter
public enum PaymentStatusEnum {
    PENDING,
//    AWAITING_QR,
    PROCESSING,
    SUCCESS,
    FAILED,
    WAITING_APPROVE,
//    EXPIRED,
//    REFUNDED,
//    CANCELLED
}
