package com.mts.backend.domain.order.entity;

import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.identifier.OrderTableId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.shared.domain.AbstractEntity;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class OrderTable extends AbstractEntity<OrderTableId> {
    @NonNull
    private OrderId orderId;
    @NonNull
    private ServiceTableId tableId;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OrderTable that)) return false;
        if (!super.equals(o)) return false;

        return orderId.equals(that.orderId) && tableId.equals(that.tableId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + orderId.hashCode();
        result = 31 * result + tableId.hashCode();
        return result;
    }

    @NonNull
    private LocalDateTime checkInTime;
    @Nullable
    private LocalDateTime checkOutTime;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    
    public OrderTable(OrderTableId id,
                      @NonNull OrderId orderId,
                      @NonNull ServiceTableId tableId,
                      @NonNull LocalDateTime checkInTime,
                      @Nullable LocalDateTime checkOutTime,
                      LocalDateTime updatedAt){
        super(id);
        this.orderId = Objects.requireNonNull(orderId, "Order id is required");
        this.tableId = Objects.requireNonNull(tableId, "Table id is required");
        this.checkInTime = Objects.requireNonNull(checkInTime, "Check in time is required");
        this.checkOutTime = checkOutTime;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        valid();
    }
    
    public LocalDateTime checkOut(){
        if (checkOutTime != null){
            throw new DomainException("Bàn đã được check out");
        }
        
        checkOutTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        valid();
        return checkOutTime;
    }
    
    public void valid(){
        if (checkInTime.isAfter(LocalDateTime.now())){
            throw new DomainException("Thời gian check in không hợp lệ");
        }
        
        if (checkOutTime != null && checkOutTime.isAfter(LocalDateTime.now())){
            throw new DomainException("Thời gian check out không hợp lệ");
        }
    }
    
    public OrderId getOrderId() {
        return orderId;
    }
    
    public ServiceTableId getTableId() {
        return tableId;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public Optional<LocalDateTime> getCheckOutTime() {
        return Optional.ofNullable(checkOutTime);}
    
    public boolean isCheckOut(){
        return checkOutTime != null;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
