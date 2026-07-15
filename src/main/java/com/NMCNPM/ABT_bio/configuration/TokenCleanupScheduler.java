package com.NMCNPM.ABT_bio.configuration;

import com.NMCNPM.ABT_bio.repository.InvalidTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final InvalidTokenRepository invalidTokenRepository;

    /**
     * Chạy định kỳ lúc 02:00 sáng mỗi ngày.
     * Cron format: Giây, Phút, Giờ, Ngày, Tháng, Thứ
     */
    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Bắt đầu dọn dẹp Blacklist Token đã hết hạn...");

        Date now = new Date();
        try {
            invalidTokenRepository.deleteAllExpiredSince(now);
            log.info("Đã dọn dẹp xong Blacklist Token!");
        } catch (Exception e) {
            log.error("Lỗi trong quá trình dọn dẹp Blacklist Token: ", e);
        }
    }
}