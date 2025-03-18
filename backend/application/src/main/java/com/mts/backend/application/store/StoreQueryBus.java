package com.mts.backend.application.store;

import com.mts.backend.application.store.query.StoreByIdQuery;
import com.mts.backend.application.store.query_handler.GetStoreByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class StoreQueryBus extends AbstractQueryBus {
    
    public StoreQueryBus(GetStoreByIdQueryHandler getStoreByIdQueryHandler){
        register(StoreByIdQuery.class, getStoreByIdQueryHandler);
    }
}
