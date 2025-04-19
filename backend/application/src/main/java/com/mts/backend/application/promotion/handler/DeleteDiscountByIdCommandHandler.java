package com.mts.backend.application.promotion.handler;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mts.backend.application.promotion.command.DeleteDiscountByIdCommand;
import com.mts.backend.domain.promotion.jpa.JpaDiscountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;

@Service
public class DeleteDiscountByIdCommandHandler implements ICommandHandler<DeleteDiscountByIdCommand, CommandResult> {

    private final JpaDiscountRepository jpaDiscountRepository;

    public DeleteDiscountByIdCommandHandler(JpaDiscountRepository jpaDiscountRepository) {
        this.jpaDiscountRepository = jpaDiscountRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(DeleteDiscountByIdCommand command) {
        var discount = jpaDiscountRepository.findById(command.getDiscountId().getValue())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy discount với id: " + command.getDiscountId()));
        
        if(discount.getCoupon() != null) {
            discount.getCoupon().setDiscount(null);
        }
        discount.setCoupon(null);
        jpaDiscountRepository.delete(discount);

        return CommandResult.success(discount.getId());
    }
}

