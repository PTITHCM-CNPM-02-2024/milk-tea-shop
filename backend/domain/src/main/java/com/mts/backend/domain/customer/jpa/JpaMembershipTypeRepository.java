package com.mts.backend.domain.customer.jpa;

import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.customer.MembershipTypeEntity;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaMembershipTypeRepository extends JpaRepository<MembershipTypeEntity, Integer> {
  @Query("select (count(m) > 0) from MembershipTypeEntity m where m.requiredPoint = :requiredPoint")
  boolean existsByRequiredPoint(@Param("requiredPoint") @NonNull Integer requiredPoint);


  @Query("select (count(m) > 0) from MembershipTypeEntity m where m.type = :type")
  boolean existsByType(@Param("type") @NonNull MemberTypeName type);

  @Query("select m from MembershipTypeEntity m where m.type = :type")
  Optional<MembershipTypeEntity> findByType(@Param("type") @NonNull MemberTypeName type);

  @Query("SELECT m FROM MembershipTypeEntity m WHERE m.requiredPoint = (SELECT MIN(m2.requiredPoint) FROM MembershipTypeEntity m2)")
  Optional<MembershipTypeEntity> findWithMinimumRequiredPoint();

  
  @Query("select (count(m) > 0) from MembershipTypeEntity m where m.id <> ?1 and m.type = ?2")
  boolean existsByIdNotAndType(@NotNull Integer id, @NotNull MemberTypeName type);
}