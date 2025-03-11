package com.mts.backend.application.product;

import com.mts.backend.application.product.command.CreateProductCommand;
import com.mts.backend.application.product.handler.CreateProductCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class ProductCommandBus extends AbstractCommandBus{
    
    public ProductCommandBus(CreateProductCommandHandler createProductCommandHandler) {
        register(CreateProductCommand.class, createProductCommandHandler);
    }
    
    
}