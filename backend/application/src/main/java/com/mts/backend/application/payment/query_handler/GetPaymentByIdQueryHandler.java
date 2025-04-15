package com.mts.backend.application.payment.query_handler;

import com.mts.backend.application.order.response.OrderBasicResponse;
import com.mts.backend.application.payment.query.PaymentByIdQuery;
import com.mts.backend.application.payment.response.PaymentDetailResponse;
import com.mts.backend.application.payment.response.PaymentMethodDetailResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

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
                .order(OrderBasicResponse.builder()
                        .orderId(payment.getOrderEntity().getId())
                        .finalAmount(payment.getOrderEntity().getFinalAmount().map(Money::getValue).orElse(null))
                        .totalAmount(payment.getOrderEntity().getTotalAmount().map(Money::getValue).orElse(null))
                        .note(payment.getOrderEntity().getCustomizeNote().orElse(null))
                        .customerId(payment.getOrderEntity().getCustomerEntity().map(CustomerEntity::getId).orElse(null))
                        .employeeId(payment.getOrderEntity().getEmployeeEntity().getId())
                        .orderStatus(payment.getOrderEntity().getStatus().map(Enum::name).orElse(null))
                        .orderTime(payment.getOrderEntity().getOrderTime())
                        .build())
                .status(payment.getStatus().map(Enum::name).orElse(null))
                .paymentTime(payment.getPaymentTime().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();

        return CommandResult.success(result);
    }
}

