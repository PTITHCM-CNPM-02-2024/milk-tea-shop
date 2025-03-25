package com.mts.backend.application.payment.provider;

import com.mts.backend.application.payment.command.PaymentTransactionCommand;
import com.mts.backend.application.payment.response.PaymentInitResponse;
import com.mts.backend.application.payment.response.PaymentResult;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.repository.IPaymentMethodRepository;
import com.mts.backend.domain.payment.repository.IPaymentRepository;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CashProvider implements IPaymentProvider{
    private final static PaymentMethodId PAYMENT_METHOD_ID = PaymentMethodId.of(5);
    private final IPaymentRepository paymentRepository;
    private final IPaymentMethodRepository paymentMethodRepository;

    public CashProvider(IPaymentRepository paymentRepository, IPaymentMethodRepository paymentMethodRepository) {
        if (paymentMethodRepository.findById(PAYMENT_METHOD_ID).isEmpty())
        {
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
    public PaymentInitResponse initPayment(Payment payment, Order order) {
        
        validWhenInit(payment, order);
        
        payment.changeStatus(PaymentStatus.PROCESSING);
        
        var paymentSaved = paymentRepository.save(payment);
        
        var message = "Vui lòng thanh toán %sVNĐ".formatted(order.getFinalAmount().get().getAmount());
        
        return PaymentInitResponse.processing(
                paymentSaved.getId().getValue(),
                message,
                order.getFinalAmount().get().getAmount());
                
        
    }
    
    private void validWhenInit(Payment payment, Order order) {
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
        
        if (order.getStatus().isPresent() && order.getStatus().get() == OrderStatus.COMPLETED)
        {
            errors.add("Không thể thanh toán cho đơn hàng đã hoàn thành");
        }
        
        if (order.getStatus().isPresent() && order.getStatus().get() == OrderStatus.CANCELLED)
        {
            errors.add("Không thể thanh toán cho đơn hàng đã hủy");
        }
        
        
        if (!errors.isEmpty())
        {
            throw new DomainBusinessLogicException(errors);
        }
        
    }

    private void validWhenDispatch(Payment payment, Order order) {
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
    public PaymentResult dispatch(Payment payment, Order order, PaymentTransactionCommand transactionCommand) {
        
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
        
        List<Payment> getOtherPayments = paymentRepository.findByOrderId(order.getId());
        
        for (var pa : getOtherPayments){
            pa.changeStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(pa);
        }


        var result = paymentRepository.save(payment);
        
        return new PaymentResult(result.getId(), transactionCommand.getTransactionId());
    }
    
    
    
    public PaymentMethodId getPaymentMethodId() {
        return PAYMENT_METHOD_ID;
    }
}
