package com.mts.backend.domain.promotion.repository;

import com.mts.backend.domain.promotion.Discount;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.value_object.DiscountName;

import java.util.List;
import java.util.Optional;

public interface IDiscountRepository {
    Optional<Discount> findById(DiscountId id);
    Discount save(Discount discount);
    
    Optional<Discount> findByName(DiscountName name);
    
    List<Discount> findAll();
    
    List<Discount> findAllActive();
    boolean existByName(DiscountName name);
    boolean existById(DiscountId id);
    boolean existByCouponId(CouponId couponId);
    Optional<Discount> findByCouponId(CouponId couponId);
}
