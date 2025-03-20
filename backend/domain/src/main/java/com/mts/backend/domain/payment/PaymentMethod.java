package com.mts.backend.domain.payment;

import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


public class PaymentMethod extends AbstractAggregateRoot<PaymentMethodId> {
    @NonNull
    private PaymentMethodName name;
    @Nullable
    private String description;
    
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public PaymentMethod(PaymentMethodId id, PaymentMethodName name, String description, LocalDateTime updatedAt) {
        super(id);
        this.name = Objects.requireNonNull(name, "Payment method name is required");
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }
    
    public PaymentMethodName getName() {
        return name;
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
    
    public boolean updateName(PaymentMethodName name) {
        if (this.name.equals(name)) {
            return false;
        }
        
        this.name = name;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean updateDescription(String description) {
        if (Objects.equals(this.description, description)) {
            return false;
        }
        
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
}
