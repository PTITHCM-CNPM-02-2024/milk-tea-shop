package com.mts.backend.application.product;

import com.mts.backend.application.product.command.CreateProductSizeCommand;
import com.mts.backend.application.product.handler.CreateProductSizeCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class ProductSizeCommandBus  extends AbstractCommandBus {
    
    public ProductSizeCommandBus(CreateProductSizeCommandHandler createProductSizeCommandHandler){
        register(CreateProductSizeCommand.class, createProductSizeCommandHandler);
    }
}
