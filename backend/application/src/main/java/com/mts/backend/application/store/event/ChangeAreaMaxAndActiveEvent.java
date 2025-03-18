package com.mts.backend.application.store.event;

import com.mts.backend.application.store.command.UpdateAreaMaxAndActiveCommand;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.value_object.MaxTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.util.Optional;

public class ChangeAreaMaxAndActiveEvent extends ApplicationEvent {
    
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @lombok.Data
    public static final class Data{
        private AreaId id;
        private MaxTable maxTable;
        private boolean isActive;
        
        public Optional<MaxTable> getMaxTable(){
            return Optional.ofNullable(maxTable);
        }
    }
    
    private final Data data;
    
    public ChangeAreaMaxAndActiveEvent(Object source, Data data){
        super(source);
        this.data = data;
    }
    
    public Data getData(){
        return data;
    }
}
