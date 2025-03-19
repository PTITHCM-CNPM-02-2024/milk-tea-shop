package com.mts.backend.application.promotion.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class DiscountCommand {
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
}
