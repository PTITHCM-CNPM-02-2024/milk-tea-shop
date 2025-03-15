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
    private CategoryName name;
    
    private String description;
    
    private CategoryId parentId;
    
    private final LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    /**
     * @param categoryId
     */
    public Category(@NonNull CategoryId categoryId, @NonNull CategoryName name, @Nullable String description,@Nullable CategoryId parentId,@Nullable LocalDateTime createdAt,@Nullable LocalDateTime updatedAt) {
        super(categoryId);
        
        Objects.requireNonNull(name, "Tên danh mục không được null");
        if (parentId != null && parentId.equals(getId())) {
            throw new DomainException("Không thể chọn" + parentId + "làm cha của chính nó");
        }
        
        this.name = name;
        this.description = description == null ? "" : description;
        this.parentId = parentId;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
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
    
    public boolean changeName(String name) {
        CategoryName newName = (CategoryName) checkAndAssign(CategoryName.create(name));
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        
        return changeName(newName);
    }
    
    public void changeDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean changeParentId(CategoryId parentId) {
        
        if (parentId != null && parentId.equals(getId())) {
            throw new DomainException("Không thể chọn" + parentId + "làm cha của chính nó");
        }
        
        if (parentId != null && parentId.equals(this.parentId)) {
            return false;
        }
        
        this.parentId = parentId;
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
