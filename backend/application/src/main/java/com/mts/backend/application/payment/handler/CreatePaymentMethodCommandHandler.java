package com.mts.backend.application.payment.handler;

import com.mts.backend.application.payment.command.CreatePaymentMethodCommand;
import com.mts.backend.domain.payment.PaymentMethod;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
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
        
        PaymentMethodName name = command.getName();
        
        verifyUniqueName(name);

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .id(PaymentMethodId.create().getValue())
                .name(name)
                .description(command.getDescription().orElse(null))
                .build();
        
        var pmSaved = paymentMethodRepository.save(paymentMethod);
        
        return CommandResult.success(pmSaved.getId());
    }
    
    private void verifyUniqueName(PaymentMethodName name){
        Objects.requireNonNull(name, "PaymentMethodName is required");
        
        if (paymentMethodRepository.existsByPaymentName(name)) {
            throw new DuplicateException("Tên phương thức thanh toán đã tồn tại");
        }
    }
}
