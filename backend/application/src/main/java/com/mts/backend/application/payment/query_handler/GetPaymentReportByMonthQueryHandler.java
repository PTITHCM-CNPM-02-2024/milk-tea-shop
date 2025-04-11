package com.mts.backend.application.payment.query_handler;


import com.mts.backend.application.payment.query.PaymentReportByMonthQuery;
import com.mts.backend.application.payment.response.PaymentReportResponse;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

@Service
public class GetPaymentReportByMonthQueryHandler implements IQueryHandler<PaymentReportByMonthQuery, CommandResult> {

    private final JpaPaymentRepository jpaPaymentRepository;

    public GetPaymentReportByMonthQueryHandler(JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public CommandResult handle(PaymentReportByMonthQuery query) {
        Objects.requireNonNull(query, "PaymentReportByMonthQuery must not be null");
        
        var start = LocalDate.of(query.getYear(), query.getMonth(), 1).atStartOfDay().atZone(ZoneId.systemDefault());
        var end = start.plusMonths(1).minusDays(1)
                .toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault());
        
        var totalPayment = jpaPaymentRepository.countByPaymentTimeBetween(
                start.toInstant(),
                end.toInstant()
        );
        
        
        var totalAmount = jpaPaymentRepository.findTotalAmountPaidByPaymentTimeBetween(
                start.toInstant(),
                end.toInstant(),
                PaymentStatus.PAID
        );
        
        var averageAmount = totalAmount.divide(BigDecimal.valueOf(totalPayment), RoundingMode.HALF_UP);

        PaymentReportResponse paymentReportResponse = PaymentReportResponse.builder()
                .totalPayment(totalPayment)
                .totalAmount(totalAmount)
                .averageAmount(averageAmount)
                .build();
        
        return CommandResult.success(paymentReportResponse);
    }
}