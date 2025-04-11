package com.mts.backend.application.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductSummaryResponse {
    private Integer id;
    private String name;
    private String description;
    private Boolean signature;
    private String image_url;
    private Integer catId;
    private Boolean available;
    private BigDecimal minPrice;
}
