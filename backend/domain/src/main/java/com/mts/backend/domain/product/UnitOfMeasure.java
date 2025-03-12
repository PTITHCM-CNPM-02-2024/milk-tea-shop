package com.mts.backend.domain.product;

import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class UnitOfMeasure extends AbstractAggregateRoot <UnitOfMeasureId> {
    @NonNull
    private UnitName name; 
    @NonNull
    private UnitSymbol symbol;
    @Nullable
    private String description;
    @NonNull
    private final LocalDateTime createdAt;
    @NonNull
    private LocalDateTime updatedAt;
    /**
     * @param unitOfMeasureId
     */
    public UnitOfMeasure (@NonNull UnitOfMeasureId  unitOfMeasureId,@NonNull UnitName name,@NonNull UnitSymbol symbol,@Nullable String description,@Nullable LocalDateTime createdAt,@Nullable LocalDateTime updatedAt) {
        super(unitOfMeasureId);
        
        this.name = Objects.requireNonNull(name, "Tên đơn vị không được null");
        this.symbol = Objects.requireNonNull(symbol, "Ký hiệu đơn vị không được null");
        this.description = description == null ? "" : description;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }
    
    public void changeName(UnitName name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeName(String name) {
        UnitName newName = (UnitName) checkAndAssign(UnitName.create(name));
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeSymbol(UnitSymbol symbol) {
        this.symbol = symbol;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeSymbol(String symbol) {
        UnitSymbol newSymbol = (UnitSymbol) checkAndAssign(UnitSymbol.create(symbol));
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public UnitName getName() {
        return name;
    }
    
    public UnitSymbol getSymbol() {
        return symbol;
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
