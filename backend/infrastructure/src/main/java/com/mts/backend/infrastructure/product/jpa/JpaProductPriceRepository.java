package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.ProductEntity;
import com.mts.backend.infrastructure.persistence.entity.ProductPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaProductPriceRepository extends JpaRepository<ProductPriceEntity, Long>, JpaSpecificationExecutor<ProductPriceEntity> {
    @Query("select p from ProductPriceEntity p where p.productEntity.id = ?1")
    Set<ProductPriceEntity> findPricesByProductId(@NonNull Integer id);

}