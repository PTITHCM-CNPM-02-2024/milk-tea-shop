package com.mts.backend.domain.store.jpa;

import com.mts.backend.domain.store.ServiceTableEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Repository
public interface JpaServiceTableRepository extends JpaRepository<ServiceTableEntity, ServiceTableId> {
  @EntityGraph(attributePaths = {"areaEntity"}, type = EntityGraph.EntityGraphType.FETCH)
  @Query("select s from ServiceTableEntity s WHERE s.active = :active")
  List<ServiceTableEntity> findByActive(@Param("active") @NonNull Boolean active);

  
  @EntityGraph(attributePaths = {"areaEntity"})
  @Query("select s from ServiceTableEntity s")
  List<ServiceTableEntity> findAllWithArea(Pageable pageable);


  @Query("select count(s) from ServiceTableEntity s where s.areaEntity.id = :id")
  long countByAreaEntity_Id(@Param("id") AreaId id);

  @Query("select s from ServiceTableEntity s where s.tableNumber = :tableNumber")
  Optional<ServiceTableEntity> findByTableNumber(@Param("tableNumber") @NonNull String tableNumber);

  @Query("select (count(s) > 0) from ServiceTableEntity s where s.tableNumber = :tableNumber")
  boolean existsByTableNumber(@Param("tableNumber") @NonNull String tableNumber);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM milk_tea_shop_prod.ServiceTable WHERE table_id = :id", nativeQuery = true)
  void deleteServiceTable(@Param("id") Integer id);

  @Query("select s from ServiceTableEntity s where s.areaEntity.id = :id")
  Set<ServiceTableEntity> findByAreaEntity_Id(@Param("id") @NonNull AreaId id);


  
  @EntityGraph(attributePaths = {"areaEntity"})
  Optional<ServiceTableEntity> findByIdWithArea(ServiceTableId serviceTableId);
}