package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.Product;

import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.infrastructure.product.jpa.JpaProductPriceRepository;
import com.mts.backend.infrastructure.product.jpa.JpaProductRepository;
import com.mts.backend.infrastructure.product.jpa.JpaProductSizeRepository;
import com.mts.backend.infrastructure.persistence.entity.CategoryEntity;
import com.mts.backend.infrastructure.persistence.entity.ProductEntity;
import com.mts.backend.infrastructure.persistence.entity.ProductPriceEntity;
import com.mts.backend.infrastructure.persistence.entity.ProductSizeEntity;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ProductRepository implements IProductRepository {
    private final ICategoryRepository categoryRepository;
    private final JpaProductRepository jpaProductRepository;
    private final JpaProductPriceRepository jpaProductPriceRepository;
    private final JpaProductSizeRepository jpaProductSizeRepository;

    public ProductRepository(ICategoryRepository categoryRepository, JpaProductRepository jpaProductRepository, JpaProductPriceRepository jpaProductPriceRepository, JpaProductSizeRepository jpaProductSizeRepository) {
        this.categoryRepository = categoryRepository;
        this.jpaProductRepository = jpaProductRepository;
        this.jpaProductPriceRepository = jpaProductPriceRepository;
        this.jpaProductSizeRepository = jpaProductSizeRepository;
    }
    /**
     * @param id 
     * @return
     */
    @Override
    public Optional<Product> findById(ProductId id) {
        Objects.requireNonNull(id, "Id không được null");
        
        ProductEntity productEntity = jpaProductRepository.findById(id.getValue()).orElse(null);
        
        
        if (productEntity == null) {
            return Optional.empty();
        }
         
        // TODO: Cần kiểm tra xem có cần thiết không
        return Optional.empty();
    }

    /**
     * @return 
     */
    @Override
    public List<Product> findAll() {
        return List.of();
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public List<Product> findByNameContaining(String name) {
        return List.of();
    }

    /**
     * @param categoryId 
     * @return
     */
    @Override
    public List<Product> findByCategoryId(CategoryId categoryId) {
        return List.of();
    }

    /**
     * @param isSignature 
     * @return
     */
    @Override
    public List<Product> findBySignature(boolean isSignature) {
        return List.of();
    }

    /**
     * @param isAvailable 
     * @return
     */
    @Override
    public List<Product> findByAvailable(boolean isAvailable) {
        return List.of();
    }

    /**
     * @param product 
     */
    @Override
    public void delete(Product product) {

    }

    /**
     * @param id 
     * @return
     */
    @Override
    public boolean existsById(ProductId id) {
        return false;
    }

    /**
     * @param product 
     * @return
     */
    @Override
    @Transactional
    public Product create(Product product) {
        try {
            Objects.requireNonNull(product, "Sản phẩm không tồn tại");
            
            CategoryEntity categoryEntity = null;
            
            if (product.getCategoryId().isPresent()) {
                Category category = categoryRepository.findById(product.getCategoryId().get()).orElseThrow(() -> new DomainException("Danh mục không tồn tại"));
                categoryEntity = new CategoryEntity();
                categoryEntity.setId(category.getId().getValue());
            }
            
            ProductEntity productEntity = new ProductEntity();
            productEntity.setName(product.getName().getValue());
            productEntity.setDescription(product.getDescription());
            productEntity.setImagePath(product.getImagePath());
            productEntity.setIsAvailable(product.isAvailable());
            productEntity.setIsSignature(product.isSignature());
            productEntity.setCategoryEntity(categoryEntity);
            
            
            productEntity = jpaProductRepository.save(productEntity);
            
            if (!product.getPrices().isEmpty()) {
                ProductEntity finalProductEntity = productEntity;
                product.getPrices().forEach((price) -> {
                    ProductPriceEntity productPriceEntity = new ProductPriceEntity();
                    productPriceEntity.setProductEntity(finalProductEntity);
                    productPriceEntity.setPrice(price.getPrice().getAmount());

                    ProductSizeEntity productSizeEntity = jpaProductSizeRepository.getReferenceById(price.getSizeId().getValue());
                    
                    productPriceEntity.setSize(productSizeEntity);
                    
                    jpaProductPriceRepository.save(productPriceEntity);
                });
            }

            return new Product(
                    ProductId.of(productEntity.getId()),
                    product.getName(),
                    product.getDescription(),
                    product.getImagePath(),
                    product.isAvailable(),
                    product.isSignature(),
                    product.getCategoryId().orElse(null),
                    product.getPrices(),
                    productEntity.getCreatedAt().orElse(null),
                    productEntity.getUpdatedAt().orElse(null)
            );
        } catch (DomainException e) {
            throw new DomainException(e.getMessage());
        } catch (Exception e) {
            throw new DomainException("Lỗi không xác định" + e.getMessage());
        }
        
    }
}
