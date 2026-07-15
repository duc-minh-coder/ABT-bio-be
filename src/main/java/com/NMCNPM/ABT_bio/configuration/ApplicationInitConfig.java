package com.NMCNPM.ABT_bio.configuration;

import com.NMCNPM.ABT_bio.entity.Category;
import com.NMCNPM.ABT_bio.entity.UserIdentity;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.enums.CategoryStatusEnum;
import com.NMCNPM.ABT_bio.enums.IdentityProviderEnum;
import com.NMCNPM.ABT_bio.enums.RoleEnum;
import com.NMCNPM.ABT_bio.enums.UserStatusEnum;
import com.NMCNPM.ABT_bio.repository.CategoryRepository;
import com.NMCNPM.ABT_bio.repository.UserIdentityRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    UserIdentityRepository userIdentityRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;

    @Bean
    @ConditionalOnProperty(prefix = "spring.datasource", value = "driver-class-name", havingValue = "org.postgresql.Driver")
    ApplicationRunner applicationRunner(
            @Value("${app.init-admin.email}") String adminEmail,
            @Value("${app.init-admin.password:}") String rawPassword
    ) {
        return args -> {
            RoleEnum adminRole = RoleEnum.ADMIN;

            Users adminUser;

            // Kiểm tra Admin
            if (userIdentityRepository.existsByProviderAndProviderUserId(IdentityProviderEnum.LOCAL, adminEmail)) {
                log.info("Admin account already exists.");
            } else {
                // Tạo mới Admin
                String password = (rawPassword == null || rawPassword.isBlank())
                        ? UUID.randomUUID() + UUID.randomUUID().toString()
                        : rawPassword;

                if (rawPassword == null || rawPassword.isBlank()) {
                    log.warn("ADMIN PASSWORD GENERATED: {}", password);
                }

                adminUser = Users.builder()
                        .fullName("ABT ADMIN")
                        .contactEmail(adminEmail)
                        .role(adminRole)
                        .verified(true)
                        .status(UserStatusEnum.ACTIVE)
                        .createdAt(Instant.now())
                        .build();
                adminUser = userRepository.save(adminUser);

                UserIdentity adminIdentity = UserIdentity.builder()
                        .user(adminUser)
                        .provider(IdentityProviderEnum.LOCAL)
                        .providerUserId(adminEmail)
                        .email(adminEmail)
                        .passwordHash(passwordEncoder.encode(password))
                        .verified(true)
                        .build();
                userIdentityRepository.save(adminIdentity);
                log.info("Admin account created.");
            }

            initCategories();
        };
    }

    private void initCategories() {
        createCategoryIfNotExists(
                "Kit xét nghiệm & Sinh phẩm",
                "kit-xet-nghiem-sinh-pham",
                "Các loại sinh phẩm chẩn đoán bệnh, tách chiết Nucleic acid, PCR và test nhanh."
        );
        createCategoryIfNotExists(
                "Thiết bị phòng thí nghiệm",
                "thiet-bi-phong-thi-nghiem",
                "Máy móc phục vụ nghiên cứu: máy khuấy từ, máy lắc, tủ sấy, tủ ấm, nồi hấp."
        );
        createCategoryIfNotExists(
                "Hóa chất & Vật tư tiêu hao",
                "hoa-chat-vat-tu-tieu-hao",
                "Hóa chất sinh học phân tử, dung dịch đệm, thuốc thử và vật tư như găng tay."
        );
        createCategoryIfNotExists(
                "Dịch vụ chuyên môn",
                "dich-vu-chuyen-mon",
                "Dịch vụ tư vấn, thiết kế và setup phòng thí nghiệm, phòng xét nghiệm đạt chuẩn."
        );
    }

    private void createCategoryIfNotExists(String name, String slug, String description) {
        if (categoryRepository.existsBySlug(slug)) {
            return;
        }
        Category category = Category.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .status(CategoryStatusEnum.ACTIVE)
                .build();
        categoryRepository.save(category);
        log.info("Category '{}' created.", name);
    }

    // Class DTO nội bộ để lưu data init bank cho gọn code
    private record BankInitData(String code, String shortName, String fullName, String bin) {}
}
