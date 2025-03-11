package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.repository.ISizeRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductSizeRepository implements ISizeRepository{

    /**
     * @param productSizeId 
     * @return
     */
    @Override
    public boolean existsById(ProductSizeId productSizeId) {
        return false;
    }
}
