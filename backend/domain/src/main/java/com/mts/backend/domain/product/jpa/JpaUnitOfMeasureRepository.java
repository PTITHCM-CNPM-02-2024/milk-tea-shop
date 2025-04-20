package com.mts.backend.domain.product.jpa;

import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, Integer> {
  @Query("select (count(u) > 0) from UnitOfMeasure u where u.symbol = :symbol")
  boolean existsBySymbol(@Param("symbol") @NonNull UnitSymbol symbol);

  @Query("select (count(u) > 0) from UnitOfMeasure u where u.name = :name")
  boolean existsByName(@Param("name") @NonNull UnitName name);

  @Query("select u from UnitOfMeasure u where u.name = ?1")
  Optional<UnitOfMeasure> findByName(@NonNull UnitName name);

  @Query("select u from UnitOfMeasure u where u.symbol = ?1")
  Optional<UnitOfMeasure> findBySymbol(@NonNull UnitSymbol symbol);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM milk_tea_shop_prod.UnitOfMeasure WHERE unit_id = :id", nativeQuery = true)
  void deleteUnitOfMeasure(@Param("id") Integer id);
  
    @Query("select (count(u) > 0) from UnitOfMeasure u where u.id <> :id and u.symbol = :symbol")
    boolean existsByIdNotAndSymbol(@Param("id") @NonNull Integer id, @Param("symbol") @NonNull UnitSymbol symbol);
    
    @Query("select (count(u) > 0) from UnitOfMeasure u where u.id <> :id and u.name = :name")
    boolean existsByIdNotAndName(@Param("id") @NonNull Integer id, @Param("name") @NonNull UnitName name);
}