package com.mts.backend.application.payment.handler;

import com.mts.backend.application.payment.command.UpdatePaymentMethodCommand;
import com.mts.backend.domain.payment.PaymentMethod;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UpdatePaymentMethodCommandHandler implements ICommandHandler<UpdatePaymentMethodCommand, CommandResult> {

    private final JpaPaymentMethodRepository paymentMethodRepository;

    private static final List<PaymentMethodId> BLACK_LIST_PAYMENT_METHOD = List.of(
            PaymentMethodId.of(1),
            PaymentMethodId.of(2),
            PaymentMethodId.of(3),
            PaymentMethodId.of(4),
            PaymentMethodId.of(5));

    public UpdatePaymentMethodCommandHandler(JpaPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdatePaymentMethodCommand command) {
        Objects.requireNonNull(command, "UpdatePaymentMethodCommand is required");

        if (BLACK_LIST_PAYMENT_METHOD.contains(command.getPaymentMethodId())) {
            throw new DomainException("Không thể cập nhật phương thức thanh toán này");
        }

        try {
            var paymentMethod = paymentMethodRepository.findById(command.getPaymentMethodId().getValue())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy phương thức thanh toán"));

            paymentMethod.setName(command.getName());

            paymentMethod.setDescription(command.getDescription().orElse(null));

            return CommandResult.success(paymentMethod.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_payment_method_name")) {
                throw new DuplicateException("Tên phương thức thanh toán đã tồn tại");
            }
            throw new DomainException("Lỗi khi cập nhật phương thức thanh toán", e);
        }
    }

}
