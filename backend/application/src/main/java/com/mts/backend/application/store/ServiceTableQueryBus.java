package com.mts.backend.application.store;

import com.mts.backend.application.store.query.DefaultServiceTableQuery;
import com.mts.backend.application.store.query.ServiceTableActiveQuery;
import com.mts.backend.application.store.query.ServiceTableByIdQuery;
import com.mts.backend.application.store.query_handler.GetAllServiceTableActiveQueryHandler;
import com.mts.backend.application.store.query_handler.GetAllServiceTableQueryHandler;
import com.mts.backend.application.store.query_handler.GetServiceTableIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class ServiceTableQueryBus extends AbstractQueryBus {
    public ServiceTableQueryBus(GetAllServiceTableQueryHandler getAllServiceTableQueryHandler,
                                GetServiceTableIdQueryHandler getServiceTableIdQueryHandler,
                                GetAllServiceTableActiveQueryHandler getAllServiceTableActiveQueryHandler){
        register(DefaultServiceTableQuery.class, getAllServiceTableQueryHandler);
        register(ServiceTableByIdQuery.class, getServiceTableIdQueryHandler);
        register(ServiceTableActiveQuery.class, getAllServiceTableActiveQueryHandler);
    }
    
}
