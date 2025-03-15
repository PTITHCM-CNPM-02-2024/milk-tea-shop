package com.mts.backend.application.product.response;

import com.mts.backend.application.product.query.DefaultCategoryQuery;
import com.mts.backend.application.product.query_handler.GetAllCategoryQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class CategoryQueryBus extends AbstractQueryBus {
    
    public CategoryQueryBus(GetAllCategoryQueryHandler getAllCategoryQueryHandler) {
        register(DefaultCategoryQuery.class, getAllCategoryQueryHandler);
    }
}
