package com.mts.backend.domain.promotion.jpa;

import com.mts.backend.domain.promotion.CouponEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JpaCouponRepository extends JpaRepository<CouponEntity, CouponId> {
    @Query("select (count(c) > 0) from CouponEntity c where c.coupon = :coupon")
    boolean existsByCoupon(@Param("coupon") @NonNull CouponCode coupon);

    @Query("select c from CouponEntity c where c.coupon = :coupon")
    Optional<CouponEntity> findByCoupon(@Param("coupon") @NonNull CouponCode coupon);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Coupon WHERE coupon_id = :id",
            nativeQuery = true)
    void deleteCoupon(@Param("id") Long id);

}