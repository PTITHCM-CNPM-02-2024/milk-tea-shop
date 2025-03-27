package com.mts.backend.domain.customer.jpa;

import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.persistence.entity.MembershipTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaMembershipTypeRepository extends JpaRepository<MembershipTypeEntity, Integer> {

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO milk_tea_shop_prod.MembershipType (membership_type_id, type,  discount_value, discount_unit, required_point, description, valid_until, is_active) " +
          "VALUES (:#{#entity.id}, :#{#entity.type}, :#{#entity.discountValue}, :#{#entity.discountUnit.name()}, :#{#entity" +
          ".requiredPoint}, :#{#entity.description}, :#{#entity.validUntil}, :#{#entity.isActive})",
          nativeQuery = true)
  void insertMembership(@Param("entity") MembershipTypeEntity entity);
  
    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.MembershipType SET " +
            "type = :#{#entity.type}, " +
            "discount_value = :#{#entity.discountValue}, " +
            "discount_unit = :#{#entity.discountUnit.name()}, " +
            "required_point = :#{#entity.requiredPoint}, " +
            "description = :#{#entity.description}, " +
            "valid_until = :#{#entity.validUntil}, " +
            "is_active = :#{#entity.isActive} " +
            "WHERE membership_type_id = :#{#entity.id}",
            nativeQuery = true)
    void updateMembership(@Param("entity") MembershipTypeEntity entity);

  @Query("select (count(m) > 0) from MembershipTypeEntity m where m.type = :type")
  boolean existsByType(@Param("type") @NonNull String type);

  @Query("select m from MembershipTypeEntity m where m.type = :type")
  Optional<MembershipTypeEntity> findByType(@Param("type") @NonNull String type);

  @Query("""
          select distinct m from MembershipTypeEntity m
          where m.discountUnit = :discountUnit and m.discountValue = :memberDiscountValue""")
  Set<MembershipTypeEntity> findDistinctByDiscountUnitAndDiscountValue(@Param("discountUnit") @NonNull DiscountUnit discountUnit, @Param("memberDiscountValue") @NonNull BigDecimal memberDiscountValue);


}