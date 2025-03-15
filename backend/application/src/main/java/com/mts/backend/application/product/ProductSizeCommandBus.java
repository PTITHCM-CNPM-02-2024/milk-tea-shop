package com.mts.backend.application.product;

import com.mts.backend.application.product.command.CreateProductSizeCommand;
import com.mts.backend.application.product.command.UpdateProductSizeCommand;
import com.mts.backend.application.product.handler.CreateProductSizeCommandHandler;
import com.mts.backend.application.product.handler.UpdateProductSizeCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class ProductSizeCommandBus  extends AbstractCommandBus {
    
    public ProductSizeCommandBus(CreateProductSizeCommandHandler createProductSizeCommandHandler, UpdateProductSizeCommandHandler updateProductSizeCommandHandler) {
        register(CreateProductSizeCommand.class, createProductSizeCommandHandler);
        register(UpdateProductSizeCommand.class, updateProductSizeCommandHandler);
    }
}
