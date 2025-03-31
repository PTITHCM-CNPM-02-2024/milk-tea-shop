package com.mts.backend.application.payment.handler;

import com.mts.backend.application.payment.command.UpdatePaymentMethodCommand;
import com.mts.backend.domain.payment.PaymentMethodEntity;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UpdatePaymentMethodCommandHandler implements ICommandHandler<UpdatePaymentMethodCommand, CommandResult> {
    
    private final JpaPaymentMethodRepository paymentMethodRepository;
    
    private static final List<PaymentMethodId> BLACK_LIST_PAYMENT_METHOD = List.of(
            PaymentMethodId.of(1),
            PaymentMethodId.of(2),
            PaymentMethodId.of(3),
            PaymentMethodId.of(4),
            PaymentMethodId.of(5)
    );
    
    public UpdatePaymentMethodCommandHandler(JpaPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdatePaymentMethodCommand command) {
        Objects.requireNonNull(command, "UpdatePaymentMethodCommand is required");
        
        if (BLACK_LIST_PAYMENT_METHOD.contains(command.getPaymentMethodId())) {
            throw new NotFoundException("Không thể cập nhật phương thức thanh toán này");
        }
        
        var paymentMethod = mustExistPaymentMethod(command.getPaymentMethodId());
        
        PaymentMethodName name = command.getName();
        
        if (paymentMethod.changeName(name)) {
            verifyUniqueName(name);
        }
        
        paymentMethod.setPaymentDescription(command.getDescription().orElse(null));
        
        var pmSaved = paymentMethodRepository.save(paymentMethod);
        
        return CommandResult.success(pmSaved.getId());
    }
    
    private PaymentMethodEntity mustExistPaymentMethod(PaymentMethodId id){
        Objects.requireNonNull(id, "PaymentMethodId is required");
        
        return paymentMethodRepository.findById(id.getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phương thức thanh toán"));
    }
    
    
    
    
    private void verifyUniqueName(PaymentMethodName name){
        Objects.requireNonNull(name, "PaymentMethodName is required");
        
        if (paymentMethodRepository.existsByPaymentName(name)) {
            throw new DuplicateException("Tên phương thức thanh toán đã tồn tại");
        }
    }
}
