package com.NMCNPM.ABT_bio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private String unit;
    private String image;
    private String description;
    private List<String> specs;
    private Integer stock;
    private Boolean featured;
    private String slug;
}
