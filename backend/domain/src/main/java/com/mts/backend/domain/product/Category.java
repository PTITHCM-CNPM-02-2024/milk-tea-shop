package com.mts.backend.domain.product;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Category extends AbstractAggregateRoot<CategoryId>{
    @NonNull
    private CategoryName name;
    @Nullable
    private String description;
    @Nullable
    private CategoryId parentId;
    
    private final LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    /**
     * @param categoryId
     */
    public Category(@NonNull CategoryId categoryId, @NonNull CategoryName name, @Nullable String description, @Nullable CategoryId parentId, @Nullable LocalDateTime updatedAt) {
        super(categoryId);
        
        
        this.name = Objects.requireNonNull(name, "Category name is required");
        this.description = description ;
        this.parentId = parentId;
        this.createdAt =  LocalDateTime.now() ;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }
    
    private void valid() {
        if (parentId != null && parentId.equals(getId())) {
            throw new DomainException("Không thể chọn" + parentId + "làm cha của chính nó");
        }
    }
    
    public boolean changeName(CategoryName name) {
        Objects.requireNonNull(name, "Category name is required");
        
        if (name.equals(this.name)) {
            return false;
        }
        
        this.name = name;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeDescription(String description) {
        
        if (Objects.equals(this.description, description)) {
            return false;
        }
        
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeParentId(CategoryId parentId) {
        
        
        if (parentId != null && parentId.equals(this.parentId)) {
            return false;
        }
        
        this.parentId = parentId;
        valid();
        
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public CategoryName getName() {
        return name;
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public Optional<CategoryId> getParentId() {
        return Optional.ofNullable(parentId);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    
}
