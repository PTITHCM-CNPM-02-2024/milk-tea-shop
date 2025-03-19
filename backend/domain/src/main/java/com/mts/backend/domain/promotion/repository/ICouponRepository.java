package com.mts.backend.domain.promotion.repository;

import com.mts.backend.domain.promotion.Coupon;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.value_object.CouponCode;

import java.util.List;
import java.util.Optional;

public interface ICouponRepository {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(CouponId id);
    List<Coupon> findAll();

    Optional<Coupon> findByCouponCode(CouponCode couponCode);
    
    boolean existByCouponCode(CouponCode name);
    
    boolean existById(CouponId id);
}
