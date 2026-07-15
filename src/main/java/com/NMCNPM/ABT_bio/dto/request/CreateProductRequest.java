package com.NMCNPM.ABT_bio.dto.request;

import com.NMCNPM.ABT_bio.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    @NotBlank
    String name;

    String slug;

    String detailedDescription;

    String thumbnailUrl;

    List<String> galleryUrls;

    Long categoryId;

    Integer inventoryCount;

    @NotNull
    BigDecimal amount;

    BigDecimal originalAmount;

    @NotNull
    Currency currency;

    String supportEmail;
    String supportTelegram;
}
