package com.mts.backend.domain.promotion.jpa;

import com.mts.backend.domain.promotion.DiscountEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface JpaDiscountRepository extends JpaRepository<DiscountEntity, Long> {
    @Query("select (count(d) > 0) from DiscountEntity d where d.couponEntity.id = :id")
    boolean existsByCouponEntity_Id(@Param("id") @NonNull Long id);

    @Query("select (count(d) > 0) from DiscountEntity d where d.name = :name")
    boolean existsByName(@Param("name") @NonNull DiscountName name);

    @Query("select d from DiscountEntity d where d.couponEntity.id = :id")
    Optional<DiscountEntity> findByCouponEntity_Id(@Param("id") @NonNull CouponId id);

    @Query("select d from DiscountEntity d where d.couponEntity.coupon = :coupon")
    Optional<DiscountEntity> findByCouponEntity_Coupon(@Param("coupon") @NonNull CouponCode coupon);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Discount WHERE discount_id = :id",
            nativeQuery = true)
    void deleteDiscount(@Param("id") Long id);

    @Query(value = "SELECT * FROM milk_tea_shop_prod.Discount WHERE name = :name LIMIT 1",
            nativeQuery = true)
    Optional<DiscountEntity> findByName(@Param("name") DiscountName name);

    @Query(value = "SELECT * FROM milk_tea_shop_prod.Discount WHERE is_active = true",
            nativeQuery = true)
    List<DiscountEntity> findAllActive();


    @Query("select (count(d) > 0) from DiscountEntity d where d.id <> ?1 and d.couponEntity.id = ?2")
    boolean existsByIdNotAndCouponEntity_Id(@NotNull Long id, @NotNull Long couponEntityId);

    @Query("select (count(d) > 0) from DiscountEntity d where d.id <> ?1 and d.name = ?2")
    boolean existsByIdNotAndName(@NotNull Long id, @NotNull DiscountName name);
}