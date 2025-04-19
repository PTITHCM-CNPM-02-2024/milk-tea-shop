package com.mts.backend.domain.customer.jpa;

import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaMembershipTypeRepository extends JpaRepository<MembershipType, Integer> {
  @Query("select (count(m) > 0) from MembershipType m where m.requiredPoint = :requiredPoint")
  boolean existsByRequiredPoint(@Param("requiredPoint") @NonNull Integer requiredPoint);


  @Query("select (count(m) > 0) from MembershipType m where m.type = :type")
  boolean existsByType(@Param("type") @NonNull String type);

  @Query("select m from MembershipType m where m.type = :type")
  Optional<MembershipType> findByType(@Param("type") @NonNull MemberTypeName type);

  @Query("SELECT m FROM MembershipType m WHERE m.requiredPoint = (SELECT MIN(m2.requiredPoint) FROM MembershipType m2)")
  Optional<MembershipType> findWithMinimumRequiredPoint();

  
  @Query("select (count(m) > 0) from MembershipType m where m.id <> ?1 and m.type = ?2")
  boolean existsByIdNotAndType(@NotNull Integer id, @NotNull String type);
  
  @EntityGraph(attributePaths = {"memberDiscountValue"})
  @Query("""
        select m from MembershipType m where m.id = :id""")
  Optional<MembershipType> findByIdFetch(@Param("id") Integer id);
}