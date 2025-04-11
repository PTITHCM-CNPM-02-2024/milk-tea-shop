package com.mts.backend.application.payment.query_handler;

import java.time.ZoneId;

import com.mts.backend.domain.common.value_object.Money;
import org.springframework.stereotype.Service;

import com.mts.backend.application.payment.query.PaymentByIdQuery;
import com.mts.backend.application.payment.response.PaymentDetailResponse;
import com.mts.backend.application.payment.response.PaymentMethodDetailResponse;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;

@Service
public class GetPaymentByIdQueryHandler implements IQueryHandler<PaymentByIdQuery, CommandResult> {
    private final JpaPaymentRepository paymentRepository;

    public GetPaymentByIdQueryHandler(JpaPaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public CommandResult handle(PaymentByIdQuery query) {
        var payment = paymentRepository.findByIdFetch(query.getPaymentId().getValue())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy thanh toán với id: " + query.getPaymentId().getValue()));

        var result = PaymentDetailResponse.builder()
            .id(payment.getId())
            .paymentMethod(PaymentMethodDetailResponse.builder()
                .id(payment.getPaymentMethod().getId())
                .name(payment.getPaymentMethod().getPaymentName().getValue())
                .description(payment.getPaymentMethod().getPaymentDescription().orElse(null))
                .build())
            .amountPaid(payment.getAmountPaid().map(Money::getValue).orElse(null))
            .change(payment.getChangeAmount().map(Money::getValue).orElse(null))
            .orderId(payment.getOrderEntity().getId())
            .status(payment.getStatus().map(Enum::name).orElse(null))
            .paymentTime(payment.getPaymentTime().atZone(ZoneId.systemDefault()).toLocalDateTime())
            .build();

        return CommandResult.success(result);
    }
}

