package com.mts.backend.application.payment.handler;

import com.mts.backend.application.payment.command.CreatePaymentMethodCommand;
import com.mts.backend.domain.payment.PaymentMethod;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreatePaymentMethodCommandHandler implements ICommandHandler<CreatePaymentMethodCommand, CommandResult> {
    private final JpaPaymentMethodRepository paymentMethodRepository;

    public CreatePaymentMethodCommandHandler(JpaPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(CreatePaymentMethodCommand command) {

        Objects.requireNonNull(command, "CreatePaymentMethodCommand is required");

        try {

            PaymentMethod paymentMethod = PaymentMethod.builder()
                    .id(PaymentMethodId.create().getValue())
                    .name(command.getName())
                    .description(command.getDescription().orElse(null))
                    .build();

            var pmSaved = paymentMethodRepository.save(paymentMethod);
            return CommandResult.success(pmSaved.getId());

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_payment_method_name")) {
                throw new DuplicateException("Tên phương thức thanh toán đã tồn tại");
            }
            throw new DomainException("Lỗi khi tạo phương thức thanh toán", e);
        }

    }
}
