package com.mts.backend.infrastructure.store.jpa;

import com.mts.backend.infrastructure.persistence.entity.ServiceTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface JpaServiceTableRepository extends JpaRepository<ServiceTableEntity, Integer> {
  @Query("select s from ServiceTableEntity s where s.tableNumber = :tableNumber")
  Optional<ServiceTableEntity> findByTableNumber(@Param("tableNumber") @NonNull String tableNumber);

  @Query("select (count(s) > 0) from ServiceTableEntity s where s.tableNumber = :tableNumber")
  boolean existsByTableNumber(@Param("tableNumber") @NonNull String tableNumber);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO milk_tea_shop_prod.ServiceTable (table_id, area_id, table_number, is_active) " +
            "VALUES (:#{#entity.id}, :#{#entity.areaEntity?.id}, :#{#entity.tableNumber}, :#{#entity.isActive})",
          nativeQuery = true)
  void insertServiceTable(@Param("entity") ServiceTableEntity entity);

  @Modifying
  @Transactional
  @Query(value = "UPDATE milk_tea_shop_prod.ServiceTable SET " +
          "area_id = :#{#entity.areaEntity?.id}, " +
          "table_number = :#{#entity.tableNumber}, " +
          "is_active = :#{#entity.isActive} " +
          "WHERE table_id = :#{#entity.id}",
          nativeQuery = true)
  void updateServiceTable(@Param("entity") ServiceTableEntity entity);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM milk_tea_shop_prod.ServiceTable WHERE table_id = :id", nativeQuery = true)
  void deleteServiceTable(@Param("id") Integer id);

  @Query("select s from ServiceTableEntity s where s.isActive = :isActive")
  List<ServiceTableEntity> findByActive(@Param("isAvailable") @NonNull Boolean isActive);

  @Query("select s from ServiceTableEntity s where s.areaEntity IS NOT NULL AND s.areaEntity.id = :id")
  Set<ServiceTableEntity> findByAreaEntity_Id(@Param("id") @NonNull Integer id);
}