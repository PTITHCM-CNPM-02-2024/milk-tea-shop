package com.mts.backend.application.product;

import com.mts.backend.application.product.query.ProductForSaleQuery;
import com.mts.backend.application.product.query.DefaultProductQuery;
import com.mts.backend.application.product.query.SignatureProductForSaleQuery;
import com.mts.backend.application.product.query_handler.GetAllAvailableOrderProductQueryHandler;
import com.mts.backend.application.product.query_handler.GetAllProductQueryHandler;
import com.mts.backend.application.product.query_handler.GetAllSignatureProductQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class ProductQueryBus extends AbstractQueryBus {
    
    public ProductQueryBus(GetAllProductQueryHandler getAllProductQueryHandler, GetAllAvailableOrderProductQueryHandler getAllAvailableOrderProductQueryHandler, GetAllSignatureProductQueryHandler getAllSignatureProductQueryHandler) {
        register(DefaultProductQuery.class, getAllProductQueryHandler);
        register(ProductForSaleQuery.class, getAllAvailableOrderProductQueryHandler);
        register(SignatureProductForSaleQuery.class, getAllSignatureProductQueryHandler);
    }
}
