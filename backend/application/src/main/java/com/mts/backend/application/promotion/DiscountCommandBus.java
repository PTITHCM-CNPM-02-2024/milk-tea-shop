package com.mts.backend.application.promotion;

import com.mts.backend.application.promotion.command.CreateDiscountCommand;
import com.mts.backend.application.promotion.command.UpdateDiscountCommand;
import com.mts.backend.application.promotion.handler.CreateDiscountCommandHandler;
import com.mts.backend.application.promotion.handler.UpdateDiscountCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class DiscountCommandBus extends AbstractCommandBus {
    public DiscountCommandBus(CreateDiscountCommandHandler createDiscountCommandHandler,
                              UpdateDiscountCommandHandler updateDiscountCommandHandler) {
        register(CreateDiscountCommand.class, createDiscountCommandHandler);
        register(UpdateDiscountCommand.class, updateDiscountCommandHandler);
    }
}
