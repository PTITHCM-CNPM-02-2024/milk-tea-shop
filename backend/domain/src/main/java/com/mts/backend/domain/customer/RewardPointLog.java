package com.mts.backend.domain.customer;

import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.RewardPointLogId;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.shared.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;

public class RewardPointLog extends AbstractAggregateRoot<RewardPointLogId> {
    
    private final OrderId orderId;
    private final CustomerId customerId;
    private int point;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    public RewardPointLog(RewardPointLogId id, OrderId orderId, CustomerId customerId, int point, LocalDateTime updatedAt) {
        super(id);
        this.orderId = orderId;
        this.customerId = customerId;
        this.point = point;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
    
    public OrderId getOrderId() {
        return orderId;
    }
    
    public CustomerId getCustomerId() {
        return customerId;
    }
    
    public int getPoint() {
        return point;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean changePoint(int point) {
        if (point == this.point) {
            return false;
        }
        this.point = point;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
}
