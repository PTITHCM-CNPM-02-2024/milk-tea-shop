package com.mts.backend.application.payment.query_handler;

import com.mts.backend.application.payment.query.DefaultPaymentMethodQuery;
import com.mts.backend.application.payment.response.PaymentMethodDetailResponse;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GetAllPaymentMethodCommandHandler implements IQueryHandler<DefaultPaymentMethodQuery, CommandResult> {
    private final JpaPaymentMethodRepository paymentMethodRepository;
    
    public GetAllPaymentMethodCommandHandler(JpaPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }
    
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(DefaultPaymentMethodQuery query) {
        Objects.requireNonNull(query, "DefaultPaymentMethodQuery must not be null");
        
        
        var paymentMethods = paymentMethodRepository.findAll();
        
        List<PaymentMethodDetailResponse> paymentMethodDetailRespons = paymentMethods.stream()
                .map(paymentMethod -> PaymentMethodDetailResponse.builder()
                        .id(paymentMethod.getId())
                        .name(paymentMethod.getName().getValue())
                        .description(paymentMethod.getDescription().orElse(null))
                        .build())
                .toList();
        
        return CommandResult.success(paymentMethodDetailRespons);
    }
}
