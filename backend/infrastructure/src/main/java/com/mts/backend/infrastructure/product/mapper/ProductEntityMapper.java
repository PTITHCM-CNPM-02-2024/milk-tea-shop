package com.mts.backend.infrastructure.product.mapper;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.mapper.AbstractBidirectionalMapper;
import com.mts.backend.infrastructure.persistence.entity.CategoryEntity;
import com.mts.backend.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class ProductEntityMapper extends AbstractBidirectionalMapper<Product, ProductEntity> {

    /**
     * @param target The target object
     * @return
     */
    @Override
    public Product mapReverse(ProductEntity target) {
        try {
            if (target.getId() == null){
                throw new NullPointerException("Id không được null");
            }
            
            Product product = new Product(
                    ProductId.of(target.getId()),
                    ProductName.of(target.getName()),
                    target.getDescription(),
                    target.getImagePath(),
                    target.getIsAvailable(),
                    target.getIsSignature(),
                    CategoryId.of(target.getCategoryEntity().getId()),
                    Set.of(),
                    target.getCreatedAt().orElse(LocalDateTime.now()),
                    target.getUpdatedAt().orElse(LocalDateTime.now())
            );
            
            return product;
        }catch (NullPointerException e){
            throw new NullPointerException("Id không được null");
        }
    }

    /**
     * @param source The source object
     * @return
     */
    @Override
    public ProductEntity map(Product source) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(source.getId().getValue());
        productEntity.setName(source.getName().getValue());
        productEntity.setImagePath(source.getImagePath());
        productEntity.setIsAvailable(source.isAvailable());
        productEntity.setIsSignature(source.isSignature());
        productEntity.setDescription(source.getDescription());
        productEntity.setCreatedAt(source.getCreatedAt());
        productEntity.setUpdatedAt(source.getUpdatedAt());
        
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(source.getCategoryId().isPresent() ? source.getCategoryId().get().getValue() : null);
        productEntity.setCategoryEntity(categoryEntity);
        
        return productEntity;
    }
}
