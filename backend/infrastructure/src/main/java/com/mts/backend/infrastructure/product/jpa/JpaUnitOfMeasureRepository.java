package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.UnitOfMeasureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUnitOfMeasureRepository extends JpaRepository<UnitOfMeasureEntity, Integer> {
  @Query("select u from UnitOfMeasureEntity u where upper(u.name) = upper(?1)")
  Optional<UnitOfMeasureEntity> findByName(@NonNull String name);

  @Query("select u from UnitOfMeasureEntity u where upper(u.symbol) = upper(?1)")
  Optional<UnitOfMeasureEntity> findBySymbol(@NonNull String symbol);
}