package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateStoreCommand;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.store.Store;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.repository.IStoreRepository;
import com.mts.backend.domain.store.value_object.Address;
import com.mts.backend.domain.store.value_object.StoreName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class UpdateStoreCommandHandler implements ICommandHandler<UpdateStoreCommand, CommandResult> {
    
    private final IStoreRepository storeRepository;
    
    public UpdateStoreCommandHandler(IStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    @Override
    public CommandResult handle(UpdateStoreCommand command) {
        Objects.requireNonNull(command, "Update store command is required");
        
        var store = mustExistStore(StoreId.of(command.getId()));
        
        store.changeStoreName(StoreName.of(command.getName()));
        store.changeAddress(Address.of(command.getAddress()));
        store.changePhoneNumber(PhoneNumber.of(command.getPhone()));
        store.changeEmail(Email.of(command.getEmail()));
        store.changeTaxCode(command.getTaxCode());
        store.changeOpenTime(command.getOpenTime());
        store.changeCloseTime(command.getCloseTime());
        
        var savedStore = storeRepository.save(store);
        
        return CommandResult.success(savedStore.getId().getValue());
    }
    
    private Store mustExistStore(StoreId id){
        Objects.requireNonNull(id, "Store id is required");
        
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cửa hàng không tồn tại"));
    }
}
