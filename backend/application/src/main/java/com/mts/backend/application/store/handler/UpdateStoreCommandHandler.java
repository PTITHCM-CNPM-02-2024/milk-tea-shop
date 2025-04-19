package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateStoreCommand;
import com.mts.backend.domain.store.Store;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.jpa.JpaStoreRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class UpdateStoreCommandHandler implements ICommandHandler<UpdateStoreCommand, CommandResult> {
    
    private final JpaStoreRepository storeRepository;
    
    public UpdateStoreCommandHandler(JpaStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    @Override
    @Transactional
    public CommandResult handle(UpdateStoreCommand command) {
        Objects.requireNonNull(command, "Update store command is required");
        
        var store = mustExistStore(command.getId());
        
        store.setAddress(command.getAddress());
        store.setEmail(command.getEmail());
        store.setName(command.getName());
        store.setPhone(command.getPhone());
        store.setTaxCode(command.getTaxCode());
        store.setOpeningTime(command.getOpenTime());
        store.setClosingTime(command.getCloseTime());
        store.setOpeningDate(command.getOpeningDate());
        
        return CommandResult.success(store.getId());
    }
    @Transactional
    protected Store mustExistStore(StoreId id){
        Objects.requireNonNull(id, "Store id is required");
        
        return storeRepository.findById(id.getValue())
                .orElseThrow(() -> new NotFoundException("Cửa hàng không tồn tại"));
    }
}
