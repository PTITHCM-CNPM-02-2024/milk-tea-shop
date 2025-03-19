package com.mts.backend.api.promotion.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class DiscountBaseRequest {
    private Long id;
    private String name;
    private String description;
    private String discountUnit;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minimumOrderValue;
    private Integer minimumRequiredProduct;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer maxUsagePerCustomer;
    private Long maxUsage;
    private Long couponId;
    private Boolean isActive;
    
}
