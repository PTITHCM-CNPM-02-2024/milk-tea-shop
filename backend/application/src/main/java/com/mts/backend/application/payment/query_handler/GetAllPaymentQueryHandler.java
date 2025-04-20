package com.mts.backend.application.payment.query_handler;

import java.time.ZoneId;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mts.backend.application.payment.query.DefaultPaymentQuery;
import com.mts.backend.application.payment.response.PaymentDetailResponse;
import com.mts.backend.application.payment.response.PaymentMethodDetailResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;

@Service
public class GetAllPaymentQueryHandler implements IQueryHandler<DefaultPaymentQuery, CommandResult> {
    private final JpaPaymentRepository paymentRepository;

    public GetAllPaymentQueryHandler(JpaPaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public CommandResult handle(DefaultPaymentQuery query) {
        var payments = paymentRepository.findAllFetch(
            PageRequest.of(query.getPage(), query.getSize())
        );

        var result = payments.map(payment -> PaymentDetailResponse.builder()
            .id(payment.getId())
            .paymentMethod(PaymentMethodDetailResponse.builder()
                .id(payment.getPaymentMethod().getId())
                .name(payment.getPaymentMethod().getName().getValue())
                .description(payment.getPaymentMethod().getDescription().orElse(null))
                .build())
            .orderId(payment.getOrder().getId())
            .paymentTime(payment.getPaymentTime().atZone(ZoneId.systemDefault()).toLocalDateTime())
            .status(payment.getStatus().map(Enum::name).orElse(null))
            .amountPaid(payment.getAmountPaid().map(Money::getValue).orElse(null))
            .change(payment.getChangeAmount().map(Money::getValue).orElse(null))
            .build());


        return CommandResult.success(result);
    }
}