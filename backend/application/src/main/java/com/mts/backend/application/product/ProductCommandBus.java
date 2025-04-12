package com.mts.backend.application.product;

import com.mts.backend.application.product.command.*;
import com.mts.backend.application.product.handler.*;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class ProductCommandBus extends AbstractCommandBus{
    
    public ProductCommandBus(CreateProductInformCommandHandler createProductCommandHandler, UpdateProductCommandHandler updateProductCommandHandler, AddProductPriceCommandHandler addProductPriceCommandHandler, UpdateProductPriceCommandHandler updateProductPriceCommandHandler, DeleteProductByIdCommandHandler deleteProductByIdCommandHandler) {
        register(CreateProductCommand.class, createProductCommandHandler);
        register(UpdateProductInformCommand.class, updateProductCommandHandler);
        register(AddProductPriceCommand.class, addProductPriceCommandHandler);
        register(UpdateProductPriceCommand.class, updateProductPriceCommandHandler);
        register(DeleteProductByIdCommand.class, deleteProductByIdCommandHandler);
    }
    
    
}