package com.mts.backend.application.payment.query_handler;

import com.mts.backend.application.order.response.OrderBasicResponse;
import com.mts.backend.application.payment.query.PaymentByIdQuery;
import com.mts.backend.application.payment.response.PaymentDetailResponse;
import com.mts.backend.application.payment.response.PaymentMethodDetailResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.domain.staff.Employee;
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
                        .name(payment.getPaymentMethod().getName().getValue())
                        .description(payment.getPaymentMethod().getDescription().orElse(null))
                        .build())
                .amountPaid(payment.getAmountPaid().map(Money::getValue).orElse(null))
                .change(payment.getChangeAmount().map(Money::getValue).orElse(null))
                .order(OrderBasicResponse.builder()
                        .orderId(payment.getOrder().getId())
                        .finalAmount(payment.getOrder().getFinalAmount().map(Money::getValue).orElse(null))
                        .totalAmount(payment.getOrder().getTotalAmount().map(Money::getValue).orElse(null))
                        .note(payment.getOrder().getCustomizeNote().orElse(null))
                        .customerId(payment.getOrder().getCustomer().map(Customer::getId).orElse(null))
                        .employeeId(payment.getOrder().getEmployee().map(Employee::getId).orElse(null))
                        .orderStatus(payment.getOrder().getStatus().map(Enum::name).orElse(null))
                        .orderTime(payment.getOrder().getOrderTime())
                        .build())
                .status(payment.getStatus().map(Enum::name).orElse(null))
                .paymentTime(payment.getPaymentTime().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();

        return CommandResult.success(result);
    }
}

