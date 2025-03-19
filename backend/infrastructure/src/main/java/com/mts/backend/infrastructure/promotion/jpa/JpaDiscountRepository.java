package com.mts.backend.infrastructure.promotion.jpa;

import com.mts.backend.infrastructure.persistence.entity.DiscountEntity;
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
    boolean existsByName(@Param("name") @NonNull String name);

    @Query("select d from DiscountEntity d where d.couponEntity.id = :id")
    Optional<DiscountEntity> findByCouponEntity_Id(@Param("id") @NonNull Long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.Discount " +
            "(discount_id, name, description, is_active, discount_type, discount_value, current_uses, max_uses, " +
            "max_uses_per_customer, valid_from, valid_until, max_discount_amount, min_required_order_value, " +
            "min_required_product, coupon_id, created_at, updated_at) " +
            "VALUES (:#{#entity.id}, :#{#entity.name}, :#{#entity.description}, :#{#entity.isActive}, " +
            ":#{#entity.discountUnit.name()}, :#{#entity.discountValue}, :#{#entity.currentUses}, :#{#entity.maxUses}," +
            " " +
            ":#{#entity.maxUsesPerCustomer}, :#{#entity.validFrom}, :#{#entity.validUntil}, " +
            ":#{#entity.maxDiscountAmount}, :#{#entity.minRequiredOrderValue}, :#{#entity.minRequiredProduct}, " +
            ":#{#entity.couponEntity.id}, " +
            "CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
            nativeQuery = true)
    void insertDiscount(@Param("entity") DiscountEntity entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.Discount SET " +
            "name = :#{#entity.name}, " +
            "description = :#{#entity.description}, " +
            "is_active = :#{#entity.isActive}, " +
            "discount_type = :#{#entity.discountUnit.name()}, " +
            "discount_value = :#{#entity.discountValue}, " +
            "current_uses = :#{#entity.currentUses}, " +
            "max_uses = :#{#entity.maxUses}, " +
            "max_uses_per_customer = :#{#entity.maxUsesPerCustomer}, " +
            "valid_from = :#{#entity.validFrom}, " +
            "valid_until = :#{#entity.validUntil}, " +
            "max_discount_amount = :#{#entity.maxDiscountAmount}, " +
            "min_required_order_value = :#{#entity.minRequiredOrderValue}, " +
            "min_required_product = :#{#entity.minRequiredProduct}, " +
            "coupon_id = :#{#entity.couponEntity.id}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE discount_id = :#{#entity.id}",
            nativeQuery = true)
    void updateDiscount(@Param("entity") DiscountEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Discount WHERE discount_id = :id",
            nativeQuery = true)
    void deleteDiscount(@Param("id") Long id);

    @Query(value = "SELECT * FROM milk_tea_shop_prod.Discount WHERE name = :name LIMIT 1",
            nativeQuery = true)
    Optional<DiscountEntity> findByName(@Param("name") String name);

    @Query(value = "SELECT * FROM milk_tea_shop_prod.Discount WHERE is_active = true",
            nativeQuery = true)
    List<DiscountEntity> findAllActive();
}