package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.ProductSizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductSizeRepository extends JpaRepository<ProductSizeEntity, Integer> {
}
