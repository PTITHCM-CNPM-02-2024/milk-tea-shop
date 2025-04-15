package com.mts.backend.application.store;

import com.mts.backend.application.store.query.AreaActiveQuery;
import com.mts.backend.application.store.query.AreaByIdQuery;
import com.mts.backend.application.store.query.DefaultAreaQuery;
import com.mts.backend.application.store.query.ServiceTableByAreaIdQuery;
import com.mts.backend.application.store.query_handler.GetAllAreaActiveQueryHandler;
import com.mts.backend.application.store.query_handler.GetAllAreaQueryHandler;
import com.mts.backend.application.store.query_handler.GetAllTblByAreaIdQueryHandler;
import com.mts.backend.application.store.query_handler.GetAreaByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class AreaQueryBus extends AbstractQueryBus {
    
    public AreaQueryBus(GetAllAreaQueryHandler getAllAreaQueryHandler,
                        GetAreaByIdQueryHandler getAreaByIdQueryHandler,
                        GetAllAreaActiveQueryHandler getAllAreaActiveQueryHandler,
                        GetAllTblByAreaIdQueryHandler getAllTblByAreaIdQueryHandler) {
        register(DefaultAreaQuery.class, getAllAreaQueryHandler);
        register(AreaByIdQuery.class, getAreaByIdQueryHandler);
        register(AreaActiveQuery.class, getAllAreaActiveQueryHandler);
        register(ServiceTableByAreaIdQuery.class, getAllTblByAreaIdQueryHandler);
    }
}
