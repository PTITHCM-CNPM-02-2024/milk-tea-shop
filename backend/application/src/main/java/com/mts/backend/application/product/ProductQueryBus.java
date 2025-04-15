package com.mts.backend.application.product;

import com.mts.backend.application.product.query.*;
import com.mts.backend.application.product.query_handler.*;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class ProductQueryBus extends AbstractQueryBus {
    
    public ProductQueryBus(GetAllProductQueryHandler getAllProductQueryHandler,
                           GetAllProductForSaleQueryHandler getAllProductForSaleQueryHandler,
                           GetAllSignatureProductQueryHandler getAllSignatureProductQueryHandler, 
                           GetAllToppingForSaleQueryHandler getAllToppingForSaleQueryHandler,
                           GetProductByIdQueryHandler getProductByIdQueryHandler) {
        register(DefaultProductQuery.class, getAllProductQueryHandler);
        register(ProductForSaleQuery.class, getAllProductForSaleQueryHandler);
        register(SignatureProductForSaleQuery.class, getAllSignatureProductQueryHandler);
        register(ToppingForSaleQuery.class, getAllToppingForSaleQueryHandler);
        register(ProdByIdQuery.class, getProductByIdQueryHandler);
    }
}
