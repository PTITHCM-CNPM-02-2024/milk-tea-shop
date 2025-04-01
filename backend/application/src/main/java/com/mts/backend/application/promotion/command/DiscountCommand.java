package com.mts.backend.application.promotion.command;

import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class DiscountCommand {
    private DiscountName name;
    private String description;
    private CouponId couponId;
    private DiscountUnit discountUnit;
    private BigDecimal discountValue;
    private Money maxDiscountAmount;
    private Money minimumOrderValue;
    private Integer minimumRequiredProduct;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer maxUsagePerCustomer;
    private Long maxUsage;
    
    public Optional<Integer> getMinimumRequiredProduct() {
        return Optional.ofNullable(minimumRequiredProduct);
    }
    
    public Optional<Long> getMaxUsage() {
        return Optional.ofNullable(maxUsage);
    }
    
    public Optional<Integer> getMaxUsagePerCustomer() {
        return Optional.ofNullable(maxUsagePerCustomer);
    }
    
    public Optional<LocalDateTime> getValidFrom() {
        return Optional.ofNullable(validFrom);
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
