package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.UnitOfMeasureEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUnitOfMeasureRepository extends JpaRepository<UnitOfMeasureEntity, Integer> {
  @Query("select u from UnitOfMeasureEntity u where upper(u.name) = upper(?1)")
  Optional<UnitOfMeasureEntity> findByName(@NonNull String name);

  @Query("select u from UnitOfMeasureEntity u where upper(u.symbol) = upper(?1)")
  Optional<UnitOfMeasureEntity> findBySymbol(@NonNull String symbol);
  
  @Modifying
  @Transactional
  @Query(value = "INSERT INTO milk_tea_shop_prod.UnitOfMeasure (name, symbol) " +
          "VALUES (:#{#entity.name}, :#{#entity.symbol})", nativeQuery = true)
  void insertUnitOfMeasure(@Param("entity") UnitOfMeasureEntity entity);
  
  @Modifying
  @Transactional
  @Query(value = "UPDATE milk_tea_shop_prod.UnitOfMeasure SET " +
          "name = :#{#entity.name}, " +
          "symbol = :#{#entity.symbol} " +
          "WHERE unit_id = :#{#entity.id}", nativeQuery = true)
  void updateUnitOfMeasure(@Param("entity") UnitOfMeasureEntity entity);
  
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM milk_tea_shop_prod.UnitOfMeasure WHERE unit_id = :id", nativeQuery = true)
  void deleteUnitOfMeasure(@Param("id") Integer id);
}