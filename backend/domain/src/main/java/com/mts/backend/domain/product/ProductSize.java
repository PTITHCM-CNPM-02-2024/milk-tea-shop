package com.mts.backend.domain.product;

import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.domain.product.value_object.QuantityOfProductSize;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class ProductSize extends AbstractAggregateRoot<ProductSizeId> {
    @NonNull
    private final LocalDateTime createdAt;
    @NonNull
    private ProductSizeName name;
    @Nullable
    private String description;
    @NonNull
    private UnitOfMeasureId unitOfMeasure;
    @NonNull
    private QuantityOfProductSize quantity;
    @NonNull
    private LocalDateTime updatedAt;

    public ProductSize(@NonNull ProductSizeId productSizeId, @NonNull ProductSizeName name, @NonNull UnitOfMeasureId unitOfMeasure, @NonNull QuantityOfProductSize quantity, String description, @Nullable LocalDateTime createdAt, @Nullable LocalDateTime updatedAt) {
        super(productSizeId);

        this.name = Objects.requireNonNull(name, "Tên kích thước sản phẩm không được null");

        this.unitOfMeasure = Objects.requireNonNull(unitOfMeasure, "Đơn vị đo kích thước không được null");

        this.quantity = Objects.requireNonNull(quantity, "Số lượng kích thước sản phẩm không được null");
        ;
        this.description = description == null ? "" : description;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }

    public boolean changeQuantity(QuantityOfProductSize quantity) {
        if (quantity.equals(this.quantity)) {
            return false;
        }
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public boolean changeQuantity(int quantity) {
        QuantityOfProductSize newQuantity = QuantityOfProductSize.of(quantity);

        if (!this.businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(this.businessErrors);
        }

        return changeQuantity(newQuantity);

    }

    public boolean changeName(ProductSizeName name) {
        Objects.requireNonNull(name, "Product size name is required");
        if (name.equals(this.name)) {
            return false;
        }

        this.name = name;
        this.updatedAt = LocalDateTime.now();
        return true;

    }

    public boolean changeName(String name) {
        ProductSizeName newName = (ProductSizeName) checkAndAssign(ProductSizeName.create(name));

        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }
        return changeName(newName);
    }

    public void changeDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void changeUnitOfMeasure(UnitOfMeasureId unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeUnitOfMeasure(int unitOfMeasure) {
        UnitOfMeasureId newUnitOfMeasure = UnitOfMeasureId.of(unitOfMeasure);

        if (!this.businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(this.businessErrors);
        }
        this.updatedAt = LocalDateTime.now();
    }

    public ProductSizeName getName() {
        return name;
    }

    public UnitOfMeasureId getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public QuantityOfProductSize getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean changeUnit(UnitOfMeasureId unitOfMeasure) {
        if (unitOfMeasure.equals(this.unitOfMeasure)) {
            return false;
        }
        this.unitOfMeasure = unitOfMeasure;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeUnit(int unitOfMeasure) {
        UnitOfMeasureId newUnitOfMeasure = UnitOfMeasureId.of(unitOfMeasure);
        
        if (!this.businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(this.businessErrors);
        }
        
        return changeUnit(newUnitOfMeasure);
    }


}
