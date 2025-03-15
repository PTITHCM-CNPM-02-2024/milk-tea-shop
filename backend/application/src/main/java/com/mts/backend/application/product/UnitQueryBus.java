package com.mts.backend.application.product;

import com.mts.backend.application.product.query.DefaultUnitQuery;
import com.mts.backend.application.product.query_handler.GetAllUnitQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class UnitQueryBus extends AbstractQueryBus {
    
    public UnitQueryBus(GetAllUnitQueryHandler getAllUnitQueryHandler) {
        register(DefaultUnitQuery.class, getAllUnitQueryHandler);
    }
}
