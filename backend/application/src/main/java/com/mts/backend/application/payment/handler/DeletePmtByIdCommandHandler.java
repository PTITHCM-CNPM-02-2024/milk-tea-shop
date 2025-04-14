package com.mts.backend.application.payment.handler;

import java.util.Objects;

import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.mts.backend.application.payment.command.DeletePmtByIdCommand;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;

@Service
public class DeletePmtByIdCommandHandler implements ICommandHandler<DeletePmtByIdCommand, CommandResult> {
    private final  JpaPaymentMethodRepository paymentMethodRepository;

    public DeletePmtByIdCommandHandler(JpaPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public CommandResult handle(DeletePmtByIdCommand command) {
        Objects.requireNonNull(command, "Payment method ID is required");


        var paymentMethod = paymentMethodRepository.findById(command.getPaymentMethodId().getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phương thức thanh toán"));


        try {
            paymentMethodRepository.delete(paymentMethod);
        }catch (DataIntegrityViolationException e) {
            throw new DomainException("Không thể xóa phương thức thanh toán, vui lòng xóa toàn bộ thanh toán liên quan");
        }
        return CommandResult.success(paymentMethod.getPaymentName());
    }
    
}
