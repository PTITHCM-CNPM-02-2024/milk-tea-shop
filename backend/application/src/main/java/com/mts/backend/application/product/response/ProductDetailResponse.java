package com.mts.backend.application.product.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductDetailResponse {
    
    private Integer id;
    
    private String name;
    
    private String description;
    
    private CategoryDetailResponse category;
    
    private Boolean signature;
    
    private String image_url;
    
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
        private String unitName;
        private String unitSymbol;
    }
    
    @Builder.Default
    private List<PriceDetail> prices = new ArrayList<>();
}
