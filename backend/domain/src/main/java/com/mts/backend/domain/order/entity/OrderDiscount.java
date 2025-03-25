package com.mts.backend.domain.order.entity;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.identifier.OrderDiscountId;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.shared.domain.AbstractEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class OrderDiscount extends AbstractEntity<OrderDiscountId> {

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OrderDiscount that)) return false;
        if (!super.equals(o)) return false;

        return orderId.equals(that.orderId) && discountId.equals(that.discountId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + orderId.hashCode();
        result = 31 * result + discountId.hashCode();
        return result;
    }

    @NonNull
    private OrderId orderId;
    @NonNull
    private DiscountId discountId;
    
    @Nullable
    private PromotionDiscountValue discountValue;
    
    @NonNull
    private Money discountAmount;
    
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    
    public OrderDiscount(OrderDiscountId id,
                         @NonNull OrderId orderId,
                         @NonNull DiscountId discountId,
                         @NonNull Money discountAmount,
                         @Nullable PromotionDiscountValue discountValue,
                         LocalDateTime updatedAt){
        super(id);
        this.orderId = Objects.requireNonNull(orderId, "Order id is required");
        this.discountId = Objects.requireNonNull(discountId, "Discount id is required");
        this.discountAmount = Objects.requireNonNull(discountAmount, "Discount amount is required");
        this.discountValue = discountValue;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
    
    public OrderId getOrderId(){
        return orderId;
    }
    
    public DiscountId getDiscountId(){
        return discountId;
    }
    
    public Money getDiscountAmount(){
        return discountAmount;
    }
    
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }
    
    public boolean changeDiscount(@NonNull DiscountId id, @NonNull PromotionDiscountValue value){
        if (this.discountId.equals(id) && Objects.equals(this.discountValue, value)){
            return false;
        }
        
        this.discountId = id;
        this.discountValue = value;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeDiscountAmount(@NonNull Money amount){
        if (this.discountAmount.equals(amount)){
            return false;
        }
        
        this.discountAmount = amount;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public Optional<PromotionDiscountValue> getDiscountValue(){
        return Optional.ofNullable(discountValue);
    }
    
    
    
    
}
