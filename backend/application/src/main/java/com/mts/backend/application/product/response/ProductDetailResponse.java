package com.mts.backend.application.product.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductDetailResponse extends ProductSummaryResponse {

    private CategorySummaryResponse category;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class PriceDetail{
        private Long id;
        private Integer sizeId;
        private String size;
        private BigDecimal price;
        private String currency;
        private Integer quantity;
        private Integer unitId;
        private String unitName;
        private String unitSymbol;
    }
    
    @Builder.Default
    private List<PriceDetail> prices = new ArrayList<>();
}
