package com.NMCNPM.ABT_bio.dto.response;

import com.NMCNPM.ABT_bio.enums.CategoryStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String image;
    private String description;
    private CategoryStatusEnum status;
    private Long productCount;
}
