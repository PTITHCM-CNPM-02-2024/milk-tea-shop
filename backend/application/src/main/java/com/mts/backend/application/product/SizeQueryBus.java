package com.mts.backend.application.product;

import com.mts.backend.application.product.query.DefaultSizeQuery;
import com.mts.backend.application.product.query_handler.GetAllSizeQueryHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class SizeQueryBus extends AbstractQueryBus {
    public SizeQueryBus(GetAllSizeQueryHandler getAllSizeQueryHandler) {
        register(DefaultSizeQuery.class, getAllSizeQueryHandler);
    }
}
