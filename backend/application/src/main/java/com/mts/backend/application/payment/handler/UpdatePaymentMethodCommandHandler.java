package com.mts.backend.application.payment.handler;

import com.mts.backend.application.payment.command.UpdatePaymentMethodCommand;
import com.mts.backend.domain.payment.PaymentMethod;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.repository.IPaymentMethodRepository;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdatePaymentMethodCommandHandler implements ICommandHandler<UpdatePaymentMethodCommand, CommandResult> {
    
    private final IPaymentMethodRepository paymentMethodRepository;
    
    public UpdatePaymentMethodCommandHandler(IPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdatePaymentMethodCommand command) {
        Objects.requireNonNull(command, "UpdatePaymentMethodCommand is required");
        
        var paymentMethod = mustExistPaymentMethod(command.getPaymentMethodId());
        
        PaymentMethodName name = command.getName();
        
        if (paymentMethod.changeName(name)) {
            verifyUniqueName(name);
        }
        
        paymentMethod.changeDescription(command.getDescription());
        
        var pmSaved = paymentMethodRepository.save(paymentMethod);
        
        return CommandResult.success(pmSaved.getId().getValue());
    }
    
    private PaymentMethod mustExistPaymentMethod(PaymentMethodId id){
        Objects.requireNonNull(id, "PaymentMethodId is required");
        
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phương thức thanh toán"));
    }
    
    
    
    
    private void verifyUniqueName(PaymentMethodName name){
        Objects.requireNonNull(name, "PaymentMethodName is required");
        
        if (paymentMethodRepository.existsByName(name)) {
            throw new DuplicateException("Tên phương thức thanh toán đã tồn tại");
        }
    }
}
