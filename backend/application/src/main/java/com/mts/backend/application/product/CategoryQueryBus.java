package com.mts.backend.application.product;

import com.mts.backend.application.product.query.DefaultCategoryQuery;
import com.mts.backend.application.product.query.CatByIdQuery;
import com.mts.backend.application.product.query.ProdByCatIdQuery;
import com.mts.backend.application.product.query.ProdByIdQuery;
import com.mts.backend.application.product.query_handler.GetAllCategoryQueryHandler;
import com.mts.backend.application.product.query_handler.GetAllProdByCatIdQueryHandler;
import com.mts.backend.application.product.query_handler.GetCatByIdQueryHandler;
import com.mts.backend.application.product.query_handler.GetProductByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class CategoryQueryBus extends AbstractQueryBus {
    
    public CategoryQueryBus(GetAllCategoryQueryHandler getAllCategoryQueryHandler,
                            GetCatByIdQueryHandler getCatByIdQueryHandler,
                            GetProductByIdQueryHandler getProductByIdQueryHandler,
                            GetAllProdByCatIdQueryHandler getAllProdByCatIdQueryHandler) {
        register(DefaultCategoryQuery.class, getAllCategoryQueryHandler);
        register(CatByIdQuery.class, getCatByIdQueryHandler);
        register(ProdByIdQuery.class, getProductByIdQueryHandler);
        register(ProdByCatIdQuery.class, getAllProdByCatIdQueryHandler);
    }
}
