package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.product.ProductSize;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.repository.IUnitRepository;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.domain.product.value_object.QuantityOfProductSize;
import com.mts.backend.infrastructure.persistence.entity.ProductSizeEntity;
import com.mts.backend.infrastructure.persistence.entity.UnitOfMeasureEntity;
import com.mts.backend.infrastructure.product.jpa.JpaProductSizeRepository;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductSizeRepository implements ISizeRepository {

    private final JpaProductSizeRepository jpaProductSizeRepository;

    public ProductSizeRepository(JpaProductSizeRepository jpaProductSizeRepository, IUnitRepository unitOfMeasureRepository) {
        this.jpaProductSizeRepository = jpaProductSizeRepository;
    }

    /**
     *
     */
    @Override
    public boolean existsById(ProductSizeId productSizeId) {
        Objects.requireNonNull(productSizeId, "ProductSizeId is required");
        return jpaProductSizeRepository.existsById(productSizeId.getValue());
    }


    protected ProductSize create(ProductSize productSize) {
        Objects.requireNonNull(productSize, "Kích thước sản phẩm không được null");

        try {
            ProductSizeEntity productSizeEntity = ProductSizeEntity.builder()
                    .name(productSize.getName().getValue())
                    .description(productSize.getDescription().orElse(""))
                    .quantity(productSize.getQuantity().getValue())
                    .unit(UnitOfMeasureEntity.builder().id(productSize.getUnitOfMeasure().getValue()).build())
                    .id(productSize.getId().getValue())
                    .build();

            jpaProductSizeRepository.insertProductSize(productSizeEntity);

            return productSize;

        } catch (Exception e) {
            throw new DomainException("Không thể tạo kích thước sản phẩm", e);
        }
    }

    /**
     * @param productSizeId
     * @return
     */
    @Override
    public Optional<ProductSize> findById(ProductSizeId productSizeId) {
        Objects.requireNonNull(productSizeId, "Id kích thước sản phẩm không được null");

        return jpaProductSizeRepository.findById(productSizeId.getValue())
                .map(p -> new ProductSize(
                        ProductSizeId.of(p.getId()),
                        ProductSizeName.of(p.getName()),
                        UnitOfMeasureId.of(p.getUnit().getId()),
                        QuantityOfProductSize.of(p.getQuantity()),
                        p.getDescription(),
                        p.getUpdatedAt().orElse(LocalDateTime.now())
                ));
    }

    /**
     * @param name
     * @return
     */
    @Override
    public Optional<ProductSize> findByName(ProductSizeName name) {
        Objects.requireNonNull(name, "Product size name is required");

        return jpaProductSizeRepository.findByName(name.getValue())
                .map(p -> new ProductSize(
                        ProductSizeId.of(p.getId()),
                        ProductSizeName.of(p.getName()),
                        UnitOfMeasureId.of(p.getUnit().getId()),
                        QuantityOfProductSize.of(p.getQuantity()),
                        p.getDescription(),
                        p.getUpdatedAt().orElse(LocalDateTime.now())
                ));
    }

    private void verifyUniqueName(ProductSizeName name) {
        Objects.requireNonNull(name, "Product size name is required");
        jpaProductSizeRepository.findByName(name.getValue())
                .ifPresent(p -> {
                    ProductSizeName existingName = ProductSizeName.of(p.getName());
                    if (existingName.equals(name)) {
                        throw new DuplicateException("Tên kích thước sản phẩm \"" + name.getValue() + "\" đã tồn tại");
                    }
                });
    }

    @Override
    @Transactional
    public ProductSize save(ProductSize productSize) {
        Objects.requireNonNull(productSize, "Product size is required");

        try {
            if (jpaProductSizeRepository.existsById(productSize.getId().getValue())) {
                return update(productSize);
            } else {
                return create(productSize);
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể lưu kích thước sản phẩm", e);
        }
    }

    /**
     * @param name
     * @param unitOfMeasure
     * @return
     */
    @Override
    public boolean existsByNameAndUnit(ProductSizeName name, UnitOfMeasureId unitOfMeasure) {
        Objects.requireNonNull(name, "Product size name is required");
        Objects.requireNonNull(unitOfMeasure, "Unit of measure is required");

        return jpaProductSizeRepository.existsByNameAndUnitId(name.getValue(), unitOfMeasure.getValue());
    }

    /**
     * @param name
     * @param unitOfMeasure
     * @return
     */
    @Override
    public Optional<ProductSize> findByNameAndUnit(ProductSizeName name, UnitOfMeasureId unitOfMeasure) {
        Objects.requireNonNull(name, "Product size name is required");
        Objects.requireNonNull(unitOfMeasure, "Unit of measure is required");

        return jpaProductSizeRepository.findByNameAndUnit(name.getValue(), unitOfMeasure.getValue())
                .map(p -> new ProductSize(
                        ProductSizeId.of(p.getId()),
                        ProductSizeName.of(p.getName()),
                        UnitOfMeasureId.of(p.getUnit().getId()),
                        QuantityOfProductSize.of(p.getQuantity()),
                        p.getDescription(),
                        p.getUpdatedAt().orElse(LocalDateTime.now())
                ));
    }

    /**
     * @return
     */
    @Override
    public List<ProductSize> findAll() {
        return jpaProductSizeRepository.findAll()
                .stream()
                .map(p -> new ProductSize(
                        ProductSizeId.of(p.getId()),
                        ProductSizeName.of(p.getName()),
                        UnitOfMeasureId.of(p.getUnit().getId()),
                        QuantityOfProductSize.of(p.getQuantity()),
                        p.getDescription(),
                        p.getUpdatedAt().orElse(LocalDateTime.now())
                ))
                .toList();
    }

    @Transactional
    protected ProductSize update(ProductSize productSize) {
        Objects.requireNonNull(productSize, "Product size is required");

        ProductSizeEntity productSizeEntity = ProductSizeEntity.builder()
                .name(productSize.getName().getValue())
                .description(productSize.getDescription().orElse(""))
                .quantity(productSize.getQuantity().getValue())
                .unit(UnitOfMeasureEntity.builder().id(productSize.getUnitOfMeasure().getValue()).build())
                .id(productSize.getId().getValue())
                .build();

        jpaProductSizeRepository.updateProductSize(productSizeEntity);

        return productSize;
 
    }
}
