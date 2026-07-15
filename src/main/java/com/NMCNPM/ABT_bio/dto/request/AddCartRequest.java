package com.NMCNPM.ABT_bio.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCartRequest {
    @NotNull
    Long productId;

    @Min(1)
    Integer quantity;
}
