package com.mts.backend.domain.customer;

import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.DiscountValue;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class MembershipType extends AbstractAggregateRoot<MembershipTypeId> {
    @NonNull
    private MemberTypeName name;
    @NonNull
    private DiscountValue discountValue;
    private int requiredPoint;
    @Nullable
    private String description;
    @NonNull
    private LocalDateTime validUntil;
    private boolean isActive;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public MembershipType(@NonNull MembershipTypeId id,@NonNull MemberTypeName name,@NonNull DiscountValue discountValue, int requiredPoint,@Nullable String description,@NonNull LocalDateTime validUntil, boolean isActive, LocalDateTime updatedAt) {
        super(id);
        
        if (requiredPoint < 0) {
            throw new IllegalArgumentException("Required point must be greater than or equal to 0");
        }
        this.name = Objects.requireNonNull(name, "Name is required");
        this.discountValue = Objects.requireNonNull(discountValue, "Discount value is required");
        this.requiredPoint = requiredPoint;
        this.description = description;
        this.validUntil = Objects.requireNonNull(validUntil, "Valid until is required");
        this.isActive = isActive;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt;
    }
    
    public MemberTypeName getName() {
        return name;
    }
    
    public DiscountValue getDiscountValue() {
        return discountValue;
    }
    
    public int getRequiredPoint() {
        return requiredPoint;
    }
    
    
    public LocalDateTime getValidUntil() {
        return validUntil;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public Optional<String> getDescription(){
        return Optional.ofNullable(description);
    }
    
    public boolean changeName(MemberTypeName name) {
        if (this.name.equals(name)) {
            return false;
        }
        this.name = name;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeDiscountValue(DiscountValue discountValue) {
        if (this.discountValue.equals(discountValue)) {
            return false;
        }
        this.discountValue = discountValue;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeRequiredPoint(int requiredPoint) {
        if (requiredPoint < 0) {
            throw new IllegalArgumentException("Required point must be greater than or equal to 0");
        }
        if (this.requiredPoint == requiredPoint) {
            return false;
        }
        this.requiredPoint = requiredPoint;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeDescription(String description) {
        if (this.description.equals(description)) {
            return false;
        }
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeValidUntil(LocalDateTime validUntil) {
        if (this.validUntil.equals(validUntil)) {
            return false;
        }
        this.validUntil = validUntil;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeActive(boolean isActive) {
        if (this.isActive == isActive) {
            return false;
        }
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    
}
