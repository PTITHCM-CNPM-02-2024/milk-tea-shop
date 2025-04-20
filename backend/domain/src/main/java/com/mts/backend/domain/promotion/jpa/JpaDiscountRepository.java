package com.mts.backend.domain.promotion.jpa;

import com.mts.backend.domain.promotion.Discount;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface JpaDiscountRepository extends JpaRepository<Discount, Long> {
    @Query("select (count(d) > 0) from Discount d where d.coupon.id = :id")
    boolean existsByCouponEntity_Id(@Param("id") @NonNull Long id);

    @Query("select (count(d) > 0) from Discount d where d.name = :name")
    boolean existsByName(@Param("name") @NonNull String name);
    

    @Query("select d from Discount d where d.coupon.coupon = :coupon")
    Optional<Discount> findByCouponEntity_Coupon(@Param("coupon") @NonNull String coupon);




    @Query("select (count(d) > 0) from Discount d where d.id <> ?1 and d.coupon.id = ?2")
    boolean existsByIdNotAndCouponEntity_Id(@NotNull Long id, @NotNull Long couponId);

    @Query("select (count(d) > 0) from Discount d where d.id <> ?1 and d.name = ?2")
    boolean existsByIdNotAndName(@NotNull Long id, @NotNull String name);
}