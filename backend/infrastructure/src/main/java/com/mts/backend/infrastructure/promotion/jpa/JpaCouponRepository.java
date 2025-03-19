package com.mts.backend.infrastructure.promotion.jpa;

import com.mts.backend.infrastructure.persistence.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JpaCouponRepository extends JpaRepository<CouponEntity, Long> {
    @Query("select (count(c) > 0) from CouponEntity c where c.coupon = :coupon")
    boolean existsByCoupon(@Param("coupon") @NonNull String coupon);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.Coupon (coupon_id, coupon, description)" +
            "VALUES (:#{#entity.id}, :#{#entity.coupon}, :#{#entity.description} " +
            ")",
            nativeQuery = true)
    void insertCoupon(@Param("entity") CouponEntity entity);

    @Query("select c from CouponEntity c where c.coupon = :coupon")
    Optional<CouponEntity> findByCoupon(@Param("coupon") @NonNull String coupon);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.Coupon SET " +
            "coupon = :#{#entity.coupon}, " +
            "description = :#{#entity.description} "
             + "WHERE coupon_id = :#{#entity.id}",
            nativeQuery = true)
    void updateCoupon(@Param("entity") CouponEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Coupon WHERE coupon_id = :id",
            nativeQuery = true)
    void deleteCoupon(@Param("id") Long id);

}