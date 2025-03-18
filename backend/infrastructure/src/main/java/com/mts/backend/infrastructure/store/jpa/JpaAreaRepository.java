package com.mts.backend.infrastructure.store.jpa;

import com.mts.backend.infrastructure.persistence.entity.AreaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface JpaAreaRepository extends JpaRepository<AreaEntity, Integer> {
    @Query("select a from AreaEntity a where a.name = :name")
    Optional<AreaEntity> findByName(@Param("name") @NonNull String name);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.Area (area_id, name, max_tables, description, is_active) " +
            "VALUES (:#{#entity.id}, :#{#entity.name}, :#{#entity.maxTables},:#{#entity.description}, :#{#entity.isActive})", nativeQuery = true)
    void insertArea(@Param("entity") AreaEntity entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.Area SET " +
            "name = :#{#entity.name}, " +
            "description = :#{#entity.description}, " +
            "is_active = :#{#entity.isActive}, " +
            "max_tables = :#{#entity.maxTables} " +
            "WHERE area_id = :#{#entity.id}",
            nativeQuery = true)
    void updateArea(@Param("entity") AreaEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Area WHERE area_id = :id", nativeQuery = true)
    void deleteArea(@Param("id") Integer id);

    @Query("select (count(a) > 0) from AreaEntity a where a.name = :name")
    boolean existsByName(@Param("name") @NonNull String name);


    @Query("select a from AreaEntity a where a.isActive = :isActive")
    List<AreaEntity> findByIsActive(@Param("isActive") @NonNull Boolean isActive);
}