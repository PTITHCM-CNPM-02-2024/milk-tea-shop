package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateStoreCommand;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.store.Store;
import com.mts.backend.domain.store.StoreEntity;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.jpa.JpaStoreRepository;
import com.mts.backend.domain.store.repository.IStoreRepository;
import com.mts.backend.domain.store.value_object.Address;
import com.mts.backend.domain.store.value_object.StoreName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
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
        
        store.changeAddress(command.getAddress());
        store.changeEmail(command.getEmail());
        store.changeStoreName(command.getName());
        store.changePhoneNumber(command.getPhone());
        store.changeTaxCode(command.getTaxCode());
        store.changeOpeningTime(command.getOpenTime());
        store.changeClosingTime(command.getCloseTime());
        store.changeOpeningDate(command.getOpeningDate());
        
        
        
        var savedStore = storeRepository.save(store);
        
        return CommandResult.success(savedStore.getId().getValue());
    }
    @Transactional
    protected StoreEntity mustExistStore(StoreId id){
        Objects.requireNonNull(id, "Store id is required");
        
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cửa hàng không tồn tại"));
    }
}
