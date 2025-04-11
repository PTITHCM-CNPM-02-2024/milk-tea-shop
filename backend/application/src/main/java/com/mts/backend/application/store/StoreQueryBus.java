package com.mts.backend.application.store;

import com.mts.backend.application.store.query.DefaultStoreQuery;
import com.mts.backend.application.store.query_handler.GetStoreQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class StoreQueryBus extends AbstractQueryBus {
    
    public StoreQueryBus(GetStoreQueryHandler getStoreByIdQueryHandler){
        register(DefaultStoreQuery.class, getStoreByIdQueryHandler);
    }
}
