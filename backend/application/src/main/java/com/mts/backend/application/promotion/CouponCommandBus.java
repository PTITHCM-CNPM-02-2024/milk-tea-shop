package com.mts.backend.application.promotion;

import com.mts.backend.application.promotion.command.CreateCouponCommand;
import com.mts.backend.application.promotion.command.UpdateCouponCommand;
import com.mts.backend.application.promotion.handler.CreateCouponCommandHandler;
import com.mts.backend.application.promotion.handler.UpdateCouponCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class CouponCommandBus extends AbstractCommandBus {
    public CouponCommandBus (CreateCouponCommandHandler createCouponCommandHandler,
                             UpdateCouponCommandHandler updateCouponCommandHandler){
    register(CreateCouponCommand.class, createCouponCommandHandler);
    register(UpdateCouponCommand.class, updateCouponCommandHandler);
    }
}
