package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.CreateStoreCommand;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.store.Store;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.repository.IStoreRepository;
import com.mts.backend.domain.store.value_object.Address;
import com.mts.backend.domain.store.value_object.StoreName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import com.mts.backend.shared.command.ICommandHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateStoreCommandHandler implements ICommandHandler<CreateStoreCommand, CommandResult> {
    private final IStoreRepository storeRepository;
    
    public CreateStoreCommandHandler(IStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(CreateStoreCommand command) {
        Objects.requireNonNull(command, "Create store command is required");
        
        var store = new Store(
                StoreId.create(),
                StoreName.of(command.getName()),
                Address.of(command.getAddress()),
                PhoneNumber.of(command.getPhone()),
                Email.of(command.getEmail()),
                command.getTaxCode(),
                command.getOpenTime(),
                command.getCloseTime(), LocalDateTime.now());
        
        var savedStore = storeRepository.save(store);
        
        return CommandResult.success(savedStore.getId().getValue());
    }
}
