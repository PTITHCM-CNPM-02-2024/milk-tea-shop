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
    public UnitOfMeasure (@NonNull UnitOfMeasureId  unitOfMeasureId, @NonNull UnitName name, @NonNull UnitSymbol symbol, @Nullable String description, @Nullable LocalDateTime updatedAt) {
        super(unitOfMeasureId);
        
        this.name = Objects.requireNonNull(name, "Unit name is required");
        this.symbol = Objects.requireNonNull(symbol, "Unit symbol is required");
        this.description = description ;
        this.createdAt =  LocalDateTime.now();
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }
    
    public boolean changeName(UnitName name) {
        Objects.requireNonNull(name, "Unit name is required");
        if (name.equals(this.name)) {
            return false;
        }
        this.name = name;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeName(String name) {
        UnitName newName = (UnitName) checkAndAssign(UnitName.create(name));
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        
        return changeName(newName);
    }
    
    public boolean changeSymbol(UnitSymbol symbol) {
        Objects.requireNonNull(symbol, "Unit symbol is required");
        
        if (symbol.equals(this.symbol)) {
            return false;
        }
        
        this.symbol = symbol;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeSymbol(String symbol) {
        UnitSymbol newSymbol = (UnitSymbol) checkAndAssign(UnitSymbol.create(symbol));
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        
        return changeSymbol(newSymbol);
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
