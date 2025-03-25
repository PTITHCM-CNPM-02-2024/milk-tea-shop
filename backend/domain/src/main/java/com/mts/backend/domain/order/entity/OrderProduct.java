package com.mts.backend.domain.order.entity;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.identifier.OrderProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.shared.domain.AbstractEntity;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class OrderProduct extends AbstractEntity<OrderProductId> {
    
    @NonNull
    private OrderId orderId;
    @NonNull
    private ProductPriceId productPriceId;

    private int quantity;
    @Nullable
    private String option; // Ice level, sugar level, etc.
    @Nullable
    private Money price; // Populated by the repository
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    
    public OrderProduct(OrderProductId id,
                        @NonNull OrderId orderId,
                        @NonNull ProductPriceId productPriceId,
                        int quantity,
                        @Nullable String option,
                        Money price,
                        LocalDateTime updatedAt){
        super(id);
        this.orderId = Objects.requireNonNull(orderId, "Order id is required");
        this.productPriceId = Objects.requireNonNull(productPriceId, "Product price id is required");
        this.quantity = quantity;
        this.option = option;
        this.price = price;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        valid();
    }
    
    private void valid(){
        if (quantity <= 0){
            throw new DomainException("Số lượng sản phẩm phải lớn hơn 0");
        }
    }

    public boolean changeQuantity(int quantity){
        if (this.quantity == quantity){
            return false;
        }
        
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
        valid();
        return true;
    }
    
    public boolean changeOption(@Nullable String option){
        if (Objects.equals(this.option, option)){
            return false;
        }
        
        this.option = option;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changePrice(@NonNull Money price){
        if (Objects.equals(this.price, price)){
            return false;
        }
        
        this.price = price;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public Optional<Money> getPrice(){
        return Optional.ofNullable(price);
    }
    
    public OrderId getOrderId(){
        return orderId;
    }
    
    public ProductPriceId getProductPriceId(){
        return productPriceId;
    }
    
    public int getQuantity(){
        return quantity;
    }
    
    public String getOption(){
        return option;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OrderProduct that)) return false;
        if (!super.equals(o)) return false;

        return orderId.equals(that.orderId) && productPriceId.equals(that.productPriceId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + orderId.hashCode();
        result = 31 * result + productPriceId.hashCode();
        return result;
    }
}
