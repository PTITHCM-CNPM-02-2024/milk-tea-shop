package com.mts.backend.domain.product.repository;

import com.mts.backend.domain.product.ProductSize;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.value_object.ProductSizeName;

import java.util.List;
import java.util.Optional;

public interface ISizeRepository {
    boolean existsById(ProductSizeId productSizeId);
    
    Optional<ProductSize> findById(ProductSizeId productSizeId);
    
    Optional<ProductSize> findByName(ProductSizeName name);

    ProductSize save(ProductSize productSize);
    
    boolean existsByNameAndUnit(ProductSizeName name, UnitOfMeasureId unitOfMeasure);
    
    Optional<ProductSize> findByNameAndUnit(ProductSizeName name, UnitOfMeasureId unitOfMeasure);

    List<ProductSize> findAll();
}
