package com.mts.backend.application.product;

import com.mts.backend.application.product.command.AddProductPriceCommand;
import com.mts.backend.application.product.command.ChangeProductInformCommand;
import com.mts.backend.application.product.command.CreateProductInformCommand;
import com.mts.backend.application.product.handler.AddProductPriceCommandHandler;
import com.mts.backend.application.product.handler.ChangeProductInformCommandHandler;
import com.mts.backend.application.product.handler.CreateProductInformCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class ProductCommandBus extends AbstractCommandBus{
    
    public ProductCommandBus(CreateProductInformCommandHandler createProductCommandHandler, ChangeProductInformCommandHandler changeProductInformCommandHandler, AddProductPriceCommandHandler addProductPriceCommandHandler) {
        register(CreateProductInformCommand.class, createProductCommandHandler);
        register(ChangeProductInformCommand.class, changeProductInformCommandHandler);
        register(AddProductPriceCommand.class, addProductPriceCommandHandler);
    }
    
    
}