package com.mts.backend.application.payment.query_handler;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mts.backend.application.payment.query.PaymentMethodByIdQuery;
import com.mts.backend.application.payment.response.PaymentMethodDetailResponse;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
@Service
public class GetPaymentMethodByIdQueryHandler implements IQueryHandler<PaymentMethodByIdQuery, CommandResult> {
    private final JpaPaymentMethodRepository paymentMethodRepository;

    public GetPaymentMethodByIdQueryHandler(JpaPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public CommandResult handle(PaymentMethodByIdQuery query) {
        
        Objects.requireNonNull(query.getPaymentMethodId(), "Payment method id is required");

        var paymentMethod = paymentMethodRepository.findById(query.getPaymentMethodId().getValue())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy phương thức thanh toán với id: " + query.getPaymentMethodId().getValue()));

        var result = PaymentMethodDetailResponse.builder()
            .id(paymentMethod.getId())
            .name(paymentMethod.getPaymentName().getValue())
            .description(paymentMethod.getPaymentDescription().orElse(null))
            .build();

        return CommandResult.success(result);
    }
}