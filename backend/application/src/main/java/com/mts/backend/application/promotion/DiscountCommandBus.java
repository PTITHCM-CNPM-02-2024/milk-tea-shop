package com.mts.backend.application.promotion;

import com.mts.backend.application.promotion.command.CreateDiscountCommand;
import com.mts.backend.application.promotion.command.UpdateDiscountCommand;
import com.mts.backend.application.promotion.handler.CreateDiscountCommandHandler;
import com.mts.backend.application.promotion.handler.UpdateDiscountCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;
import com.mts.backend.application.promotion.command.DeleteDiscountByIdCommand;
import com.mts.backend.application.promotion.handler.DeleteDiscountByIdCommandHandler;

@Component
public class DiscountCommandBus extends AbstractCommandBus {
    public DiscountCommandBus(CreateDiscountCommandHandler createDiscountCommandHandler,
                              UpdateDiscountCommandHandler updateDiscountCommandHandler,
                              DeleteDiscountByIdCommandHandler deleteDiscountByIdCommandHandler) {
        register(CreateDiscountCommand.class, createDiscountCommandHandler);
        register(UpdateDiscountCommand.class, updateDiscountCommandHandler);
        register(DeleteDiscountByIdCommand.class, deleteDiscountByIdCommandHandler);
    }
}
