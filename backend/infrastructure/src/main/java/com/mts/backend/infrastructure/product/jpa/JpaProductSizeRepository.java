package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.ProductSizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProductSizeRepository extends JpaRepository<ProductSizeEntity, Integer> {
    @Query("select p from ProductSizeEntity p where upper(p.name) = upper(?1)")
    Optional<ProductSizeEntity> findByName(@NonNull String name);
}
