package com.mts.backend.domain.store;

import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Area extends AbstractAggregateRoot<AreaId> {
    
    @NonNull
    private AreaName areaName;
    @Nullable
    private String description;
    @Nullable
    private MaxTable maxTable;
    private boolean isActive;
    private LocalDateTime updatedAt;
    private final LocalDateTime createdAt;
    
    public Area(AreaId id,
                @NonNull AreaName areaName,
                @Nullable String description,
                @Nullable MaxTable maxTable,
                boolean isActive,
                LocalDateTime updatedAt){
        super(id);
        this.areaName = Objects.requireNonNull(areaName, "Area name is required");
        this.description = description;
        this.maxTable = maxTable;
        this.isActive = isActive;
        this.updatedAt = updatedAt;
        this.createdAt = LocalDateTime.now();
    }
    
    public boolean changeAreaName(AreaName areaName){
        if (this.areaName.equals(areaName)){
            return false;
        }
        
        this.areaName = areaName;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeDescription(String description){
        if (Objects.equals(this.description, description)){
            return false;
        }
        
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeMaxTable(MaxTable maxTable){
        if (Objects.equals(this.maxTable, maxTable)){
            return false;
        }
        
        this.maxTable = maxTable;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeIsActive(boolean isActive){
        if (this.isActive == isActive){
            return false;
        }
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public AreaName getAreaName(){
        return areaName;
    }
    
    public Optional<String> getDescription(){
        return Optional.ofNullable(description);
    }
    
    public Optional<MaxTable> getMaxTable(){
        return Optional.ofNullable(maxTable);
    }
    
    public boolean isActive(){
        return isActive;
    }
    
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }
    
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    
    
    
    
    
}
