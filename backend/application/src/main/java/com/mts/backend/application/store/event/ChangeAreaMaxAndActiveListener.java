package com.mts.backend.application.store.event;

import com.mts.backend.application.store.handler.UpdateAreaMaxAndActiveCommandHandler;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ChangeAreaMaxAndActiveListener implements ApplicationListener<ChangeAreaMaxAndActiveEvent> {
    private IServiceTableRepository serviceTableRepository;
    
    public ChangeAreaMaxAndActiveListener(IServiceTableRepository serviceTableRepository) {
        this.serviceTableRepository = serviceTableRepository;
    }
    /**
     * @param event 
     */
    @Override
    public void onApplicationEvent(ChangeAreaMaxAndActiveEvent event) {
        var eventCommand = Objects.requireNonNull(event.getData(), "Event command is required");
        
        if (!(event.getSource() instanceof  UpdateAreaMaxAndActiveCommandHandler)){
            return;
        }
        
        var listTable = serviceTableRepository.findAllByAreaId(eventCommand.getId());
        
        if (listTable.isEmpty()){
            return;
        }
        
        if (event.getData().getMaxTable().isPresent() && event.getData().getMaxTable().get().getValue() < listTable.size()){
            throw new DomainException("Tổng số bàn hiện tại:" + listTable.size() + " lớn hơn số bàn tối đa:" + event.getData().getMaxTable().get().getValue());
        }
        
        listTable.forEach(table -> {
            table.changeIsActive(eventCommand.isActive());
            serviceTableRepository.save(table);
        });
        
    }

    /**
     * @return 
     */
    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
