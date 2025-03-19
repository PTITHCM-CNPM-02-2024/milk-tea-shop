package com.mts.backend.application.promotion.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DiscountDetailResponse {
    private Long id;
    private String name;
    private String description;
    private Long couponId;
    private String discountUnit;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minimumOrderValue;
    private Integer minimumRequiredProduct;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer maxUsagePerCustomer;
    private Long maxUsage;
    private Long currentUsage;
    private Boolean isActive;
}
