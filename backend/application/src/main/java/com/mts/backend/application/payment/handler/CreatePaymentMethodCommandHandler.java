package com.mts.backend.application.payment.handler;

import com.mts.backend.application.payment.command.CreatePaymentMethodCommand;
import com.mts.backend.domain.payment.PaymentMethod;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.repository.IPaymentMethodRepository;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreatePaymentMethodCommandHandler implements ICommandHandler<CreatePaymentMethodCommand, CommandResult> {
    private final IPaymentMethodRepository paymentMethodRepository;
    
    public CreatePaymentMethodCommandHandler(IPaymentMethodRepository paymentMethodRepository) {
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

        PaymentMethod paymentMethod = new PaymentMethod(
                PaymentMethodId.create(),
                name,
                command.getDescription(),
                LocalDateTime.now()
        );
        
        var pmSaved = paymentMethodRepository.save(paymentMethod);
        
        return CommandResult.success(pmSaved.getId().getValue());
    }
    
    private void verifyUniqueName(PaymentMethodName name){
        Objects.requireNonNull(name, "PaymentMethodName is required");
        
        if (paymentMethodRepository.existsByName(name)) {
            throw new DuplicateException("Tên phương thức thanh toán đã tồn tại");
        }
    }
}
