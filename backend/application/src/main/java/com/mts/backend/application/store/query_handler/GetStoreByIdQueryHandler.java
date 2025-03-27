package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.StoreByIdQuery;
import com.mts.backend.application.store.response.StoreDetailResponse;
import com.mts.backend.domain.store.Store;
import com.mts.backend.domain.store.StoreEntity;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.jpa.JpaStoreRepository;
import com.mts.backend.domain.store.repository.IStoreRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetStoreByIdQueryHandler implements IQueryHandler<StoreByIdQuery, CommandResult> {
    
    private final JpaStoreRepository storeRepository;
    
    public GetStoreByIdQueryHandler(JpaStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(StoreByIdQuery query) {
        Objects.requireNonNull(query, "Store by id query is required");
        
        var store = mustExistStore(query.getId());
        
        var response = StoreDetailResponse.builder()
                .id(store.getId().getValue())
                .name(store.getName().getValue())
                .address(store.getAddress().getValue())
                .phone(store.getPhone().getValue())
                .email(store.getEmail().getValue())
                .taxCode(store.getTaxCode())
                .openTime(store.getOpeningTime())
                .closeTime(store.getClosingTime())
                .build();
        
        return CommandResult.success(response);
    }
    
    private StoreEntity mustExistStore(StoreId id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new DomainException("Cửa hàng" + id.getValue() + " không tồn tại"));
    }
}
