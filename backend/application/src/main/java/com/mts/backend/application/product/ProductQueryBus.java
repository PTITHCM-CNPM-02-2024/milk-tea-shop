package com.mts.backend.application.product;

import com.mts.backend.application.product.query.ProductForSaleQuery;
import com.mts.backend.application.product.query.DefaultProductQuery;
import com.mts.backend.application.product.query.SignatureProductForSaleQuery;
import com.mts.backend.application.product.query.ToppingForSaleQuery;
import com.mts.backend.application.product.query_handler.GetAllProductForSaleQueryHandler;
import com.mts.backend.application.product.query_handler.GetAllProductQueryHandler;
import com.mts.backend.application.product.query_handler.GetAllSignatureProductQueryHandler;
import com.mts.backend.application.product.query_handler.GetAllToppingForSaleQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class ProductQueryBus extends AbstractQueryBus {
    
    public ProductQueryBus(GetAllProductQueryHandler getAllProductQueryHandler,
                           GetAllProductForSaleQueryHandler getAllProductForSaleQueryHandler,
                           GetAllSignatureProductQueryHandler getAllSignatureProductQueryHandler, 
                           GetAllToppingForSaleQueryHandler getAllToppingForSaleQueryHandler) {
        register(DefaultProductQuery.class, getAllProductQueryHandler);
        register(ProductForSaleQuery.class, getAllProductForSaleQueryHandler);
        register(SignatureProductForSaleQuery.class, getAllSignatureProductQueryHandler);
        register(ToppingForSaleQuery.class, getAllToppingForSaleQueryHandler);
    }
}
