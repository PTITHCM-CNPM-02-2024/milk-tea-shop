package com.mts.backend.application.product;

import com.mts.backend.application.product.command.CreateCategoryCommand;
import com.mts.backend.application.product.handler.CreateCategoryCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class CategoryCommandBus extends AbstractCommandBus {
    
    public CategoryCommandBus(CreateCategoryCommandHandler createCategoryCommandHandler) {
        register(CreateCategoryCommand.class, createCategoryCommandHandler);
    }
}
