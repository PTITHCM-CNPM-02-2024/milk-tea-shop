package com.mts.backend.domain.promotion.jpa;

import com.mts.backend.domain.promotion.Coupon;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface JpaCouponRepository extends JpaRepository<Coupon, Long> {
    @Query("select (count(c) > 0) from Coupon c where c.coupon = :coupon")
    boolean existsByCoupon(@Param("coupon") @NonNull String coupon);

    @Query("select c from Coupon c where c.coupon = :coupon")
    Optional<Coupon> findByCoupon(@Param("coupon") @NonNull String coupon);


    boolean existsByIdNotAndCoupon(@NotNull Long id, @NotNull String coupon);


    @Query("SELECT c FROM Coupon c WHERE c.discount IS NULL")
    Slice<Coupon> findAllUnusedCoupons(Pageable pageable);
}