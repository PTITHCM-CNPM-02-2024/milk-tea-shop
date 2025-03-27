package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.CreateStoreCommand;
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
import com.mts.backend.shared.command.ICommand;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateStoreCommandHandler implements ICommandHandler<CreateStoreCommand, CommandResult> {
    private final JpaStoreRepository storeRepository;
    
    public CreateStoreCommandHandler(JpaStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreateStoreCommand command) {
        Objects.requireNonNull(command, "Create store command is required");
        
        if (storeRepository.countByIdNotNull() > 0){
            throw new DomainException("Thông tin cửa hàng là duy nhất không thể tạo thêm");
        }
        
        var store = StoreEntity.builder()
                .id(StoreId.create())
                .name(command.getName())
                .address(command.getAddress())
                .phone(command.getPhone())
                .email(command.getEmail())
                .taxCode(command.getTaxCode())
                .openingTime(command.getOpenTime())
                .closingTime(command.getCloseTime())
                .openingDate(command.getOpeningDate())
                .build();
        
        var savedStore = storeRepository.save(store);
                
        
        return CommandResult.success(savedStore.getId().getValue());
    }
}
