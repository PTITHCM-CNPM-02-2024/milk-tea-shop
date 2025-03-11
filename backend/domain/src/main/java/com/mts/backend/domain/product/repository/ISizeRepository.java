package com.mts.backend.domain.product.repository;

import com.mts.backend.domain.product.identifier.ProductSizeId;

public interface ISizeRepository {
    boolean existsById(ProductSizeId productSizeId);
}
