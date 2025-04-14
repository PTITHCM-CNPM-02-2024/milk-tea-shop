package com.mts.backend.application.promotion.handler;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mts.backend.application.promotion.command.DeleteCouponByIdCommand;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;

@Service
public class DeleteCouponByIdCommandHandler implements ICommandHandler<DeleteCouponByIdCommand, CommandResult> {

    private final JpaCouponRepository jpaCouponRepository;

    public DeleteCouponByIdCommandHandler(JpaCouponRepository jpaCouponRepository) {
        this.jpaCouponRepository = jpaCouponRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(DeleteCouponByIdCommand command) {
        var coupon = jpaCouponRepository.findById(command.getCouponId().getValue())
            .orElseThrow(() -> new NotFoundException("Coupon not found"));

        coupon.getDiscount().setCouponEntity(null);
        
        jpaCouponRepository.delete(coupon);

        return CommandResult.success(coupon.getId());
    }
}
