package com.mts.backend.application.payment.provider;

import com.mts.backend.application.payment.command.PaymentTransactionCommand;
import com.mts.backend.application.payment.response.PaymentInitResponse;
import com.mts.backend.application.payment.response.PaymentResult;
import com.mts.backend.domain.order.OrderEntity;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.PaymentEntity;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CashProvider implements IPaymentProvider{
    private final static PaymentMethodId PAYMENT_METHOD_ID = PaymentMethodId.of(1);
    private final JpaPaymentRepository paymentRepository;
    private final JpaPaymentMethodRepository paymentMethodRepository;

    public CashProvider(JpaPaymentRepository paymentRepository, JpaPaymentMethodRepository paymentMethodRepository) {
        if (!paymentMethodRepository.existsById(PAYMENT_METHOD_ID.getValue())) {
            throw new DomainException("Phương thức thanh toán không tồn tại");
        }
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * @param payment
     * @param order
     * @return
     */
    @Override
    @Transactional
    public PaymentInitResponse initPayment(PaymentEntity payment, OrderEntity order) {
        
        validWhenInit(payment, order);
        
        payment.changeStatus(PaymentStatus.PROCESSING);
        
        var savedPayment = paymentRepository.save(payment);
        
        var message = "Vui lòng thanh toán %sVNĐ".formatted(order.getFinalAmount().get().getValue());
        
        return PaymentInitResponse.processing(
                savedPayment.getId(),
                message,
                order.getFinalAmount().get().getValue());
                
        
    }
    
    private void validWhenInit(PaymentEntity payment, OrderEntity order) {
        Objects.requireNonNull(payment, "Payment is required");
        Objects.requireNonNull(order, "Final amount is required");
        List<String> errors = new ArrayList<>();
        
        if (payment.getStatus().isPresent())
        {
            errors.add("Trạng thái thanh toán không hợp lệ");
        }
        
        if (payment.getAmountPaid().isPresent())
        {
            errors.add("Thanh toán đã được thực hiện");
        }
        
        if (order.getStatus().isPresent() && order.getStatus().get() == OrderStatus.COMPLETED && order.getStatus().get() == OrderStatus.CANCELLED)
        {
            errors.add("Không thể thanh toán cho đơn hàng đã hoàn thành hoặc đã hủy");
        }
        
        
        if (!errors.isEmpty())
        {
            throw new DomainBusinessLogicException(errors);
        }
        
    }

    private void validWhenDispatch(PaymentEntity payment, OrderEntity order) {
        Objects.requireNonNull(payment, "Payment is required");
        Objects.requireNonNull(order, "Order is required");
        List<String> errors = new ArrayList<>();

        // Check if payment has been initialized
        if (payment.getStatus().isEmpty()) {
            errors.add("Thanh toán chưa được khởi tạo");
        } else if (payment.getStatus().get() != PaymentStatus.PROCESSING) {
            errors.add("Thanh toán đang ở trạng thái không hợp lệ");
        }

        // Check if payment has already been processed
        if (payment.getAmountPaid().isPresent()) {
            errors.add("Thanh toán đã được thực hiện");
        }

        // Check order status - fix the logic issue in the original
        if (order.getStatus().isPresent()) {
            OrderStatus status = order.getStatus().get();
            if (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) {
                errors.add("Không thể thanh toán cho đơn hàng đã hoàn thành hoặc đã hủy");
            }
        }

        // Check if order has a valid amount
        if (order.getFinalAmount().isEmpty()) {
            errors.add("Không thể thanh toán cho đơn hàng không có giá trị");
        }

        // Throw exception if any validation errors occurred
        if (!errors.isEmpty()) {
            throw new DomainBusinessLogicException(errors);
        }
    }

    /**
     * @param payment
     * @param order
     * @param transactionCommand
     * @return
     */
    @Override
    @Transactional
    public PaymentResult dispatch(PaymentEntity payment, OrderEntity order, PaymentTransactionCommand transactionCommand) {
        
        validWhenDispatch(payment, order);
        
        
        if (transactionCommand.getAmount().compareTo(order.getFinalAmount().get()) < 0)
        {
            throw new DomainException("Số tiền thanh toán không đủ");
        }
        
        payment.changeAmountPaid(transactionCommand.getAmount());
        
        if (transactionCommand.getAmount().compareTo(order.getFinalAmount().get()) > 0)
        {
            payment.changeChangeAmount(transactionCommand.getAmount().subtract(order.getFinalAmount().get()));
        }

        payment.changeStatus(PaymentStatus.PAID);
        
        List<PaymentEntity> getOtherPayments = paymentRepository.findByOrderEntity_IdAndIdNot(order.getId(), payment.getId());
        
        for (var pa : getOtherPayments){
            pa.changeStatus(PaymentStatus.CANCELLED);
        }
        
        
        return new PaymentResult(PaymentId.of(payment.getId()), transactionCommand.getTransactionId());
    }
    
    
    
    public PaymentMethodId getPaymentMethodId() {
        return PAYMENT_METHOD_ID;
    }
}
