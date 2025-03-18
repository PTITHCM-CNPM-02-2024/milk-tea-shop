package com.mts.backend.domain.store;

import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.value_object.TableNumber;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class ServiceTable extends AbstractAggregateRoot<ServiceTableId> {
    @NonNull
    private TableNumber tableNumber;
    private boolean isActive;
    @Nullable
    private AreaId areaId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ServiceTable(ServiceTableId id,
                        @NonNull TableNumber tableNumber,
                        boolean isActive,
                        @Nullable AreaId areaId,
                        LocalDateTime updatedAt){
        super(id);
        this.tableNumber = Objects.requireNonNull(tableNumber, "Table number is required");
        this.isActive = isActive;
        this.areaId = areaId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt;
    }
    
    public boolean changeTableNumber(TableNumber tableNumber){
        if (this.tableNumber.equals(tableNumber)){
            return false;
        }
        
        this.tableNumber = tableNumber;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public void changeIsActive(boolean isActive){
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean changeAreaId(AreaId areaId){
        if (Objects.equals(this.areaId, areaId)){
            return false;
        }
        
        this.areaId = areaId;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public TableNumber getTableNumber(){
        return tableNumber;
    }
    
    public boolean isActive(){
        return isActive;
    }
    
    public Optional<AreaId> getAreaId(){
        return Optional.ofNullable(areaId);
    }
    
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }
    
}
