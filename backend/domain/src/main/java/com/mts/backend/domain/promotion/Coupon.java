package com.mts.backend.domain.promotion;

import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

public class Coupon extends AbstractAggregateRoot<CouponId> {
    @NonNull
    private CouponCode coupon;
    @Nullable
    private String description;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    
    public Coupon(@NonNull CouponId id,@NonNull CouponCode coupon,@Nullable String description,
                  LocalDateTime updatedAt) {
        super(id);
        this.coupon = Objects.requireNonNull(coupon, "Coupon code must not be null");
        this.description = description;
    }
    
    public boolean changeDescription(@Nullable String description){
        if (Objects.equals(this.description, description)){
            return false;
        }
        
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public boolean changeCoupon(@NonNull CouponCode coupon){
        if (Objects.equals(this.coupon, coupon)){
            return false;
        }
        
        this.coupon = coupon;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public CouponCode getCoupon() {
        return coupon;
    }
    
    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    
}
