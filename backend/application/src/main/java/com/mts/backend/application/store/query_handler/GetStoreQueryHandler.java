package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.DefaultStoreQuery;
import com.mts.backend.application.store.response.StoreDetailResponse;
import com.mts.backend.domain.store.jpa.JpaStoreRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetStoreQueryHandler implements IQueryHandler<DefaultStoreQuery, CommandResult> {
    
    private final JpaStoreRepository storeRepository;
    
    public GetStoreQueryHandler(JpaStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(DefaultStoreQuery query) {
        Objects.requireNonNull(query, "Store by id query is required");
        
        var store = storeRepository.findAll().stream().findFirst().orElseThrow(() -> new DomainException("Không tìm " +
                                                                                                         "thấy thông tin cửa hàng"));
        
        var response = StoreDetailResponse.builder()
                .id(store.getId())
                .name(store.getName().getValue())
                .address(store.getAddress().getValue())
                .phone(store.getPhone().getValue())
                .email(store.getEmail().getValue())
                .taxCode(store.getTaxCode())
                .openingDate(store.getOpeningDate())
                .openTime(store.getOpeningTime())
                .closeTime(store.getClosingTime())
                .build();
        
        return CommandResult.success(response);
    }
}
