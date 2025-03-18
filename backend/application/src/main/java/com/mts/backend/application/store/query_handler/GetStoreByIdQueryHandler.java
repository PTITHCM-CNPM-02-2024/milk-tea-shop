package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.StoreByIdQuery;
import com.mts.backend.application.store.response.StoreDetailResponse;
import com.mts.backend.domain.store.Store;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.repository.IStoreRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetStoreByIdQueryHandler implements IQueryHandler<StoreByIdQuery, CommandResult> {
    
    private final IStoreRepository storeRepository;
    
    public GetStoreByIdQueryHandler(IStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(StoreByIdQuery query) {
        var storeId = StoreId.of(query.getId());
        
        var store = mustExistStore(storeId);
        
        var response = StoreDetailResponse.builder()
                .id(store.getId().getValue())
                .name(store.getStoreName().getValue())
                .address(store.getAddress().getValue())
                .phone(store.getPhoneNumber().getValue())
                .email(store.getEmail().getValue())
                .taxCode(store.getTaxCode())
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .build();
        
        return CommandResult.success(response);
    }
    
    private Store mustExistStore(StoreId id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }
}
