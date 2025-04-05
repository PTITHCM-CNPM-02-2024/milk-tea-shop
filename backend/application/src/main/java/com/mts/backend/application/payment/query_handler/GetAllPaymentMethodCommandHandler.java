package com.mts.backend.application.payment.query_handler;

import com.mts.backend.application.payment.query.DefaultPaymentMethodQuery;
import com.mts.backend.application.payment.response.PaymentMethodResponse;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
        
        List<PaymentMethodResponse> paymentMethodResponses = paymentMethods.stream()
                .map(paymentMethod -> PaymentMethodResponse.builder()
                        .id(paymentMethod.getId())
                        .name(paymentMethod.getPaymentName().getValue())
                        .description(paymentMethod.getPaymentDescription().orElse(null))
                        .build())
                .toList();
        
        return CommandResult.success(paymentMethodResponses);
    }
}
