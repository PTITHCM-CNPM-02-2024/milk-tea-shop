package com.mts.backend.domain.payment.repository;

import com.mts.backend.domain.payment.PaymentMethod;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.domain.persistence.entity.PaymentMethodEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentMethodRepository implements IPaymentMethodRepository {
    
    private final JpaPaymentMethodRepository paymentMethodRepository;

    public PaymentMethodRepository(JpaPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }
    /**
     * @param 
     * @return
     */
    @Override
    @Transactional
    public PaymentMethod save(PaymentMethod paymentMethod) {
        Objects.requireNonNull(paymentMethod, "PaymentMethod is required");
        
        try {
            if (paymentMethodRepository.existsById(paymentMethod.getId().getValue())) {
                return update(paymentMethod);
            }
            return create(paymentMethod);
        }catch (Exception e) {
            throw new RuntimeException("Không thể lưu phương thức thanh toán", e);
        }
    }
    
    @Transactional
    protected PaymentMethod create(PaymentMethod paymentMethod) {
        Objects.requireNonNull(paymentMethod, "PaymentMethod is required");

        PaymentMethodEntity en = PaymentMethodEntity.builder()
                .id(paymentMethod.getId().getValue())
                .paymentName(paymentMethod.getName().getValue())
                .paymentDescription(paymentMethod.getDescription().orElse(null))
                .build();
        
        paymentMethodRepository.insertPaymentMethod(en);
        
        return paymentMethod;
    }
    
    @Transactional
    protected PaymentMethod update(PaymentMethod paymentMethod) {
        Objects.requireNonNull(paymentMethod, "PaymentMethod is required");

        PaymentMethodEntity en = PaymentMethodEntity.builder()
                .id(paymentMethod.getId().getValue())
                .paymentName(paymentMethod.getName().getValue())
                .paymentDescription(paymentMethod.getDescription().orElse(null))
                .build();
        
        paymentMethodRepository.updatePaymentMethod(en);
        
        return paymentMethod;
    }

    /**
     * @param id 
     * @return
     */
    @Override
    public Optional<PaymentMethod> findById(PaymentMethodId id) 
    {
        Objects.requireNonNull(id, "PaymentMethodId is required");
        
        return paymentMethodRepository.findById(id.getValue())
                .map(en -> new PaymentMethod(
                        PaymentMethodId.of(en.getId()),
                        PaymentMethodName.of(en.getPaymentName()),
                        en.getPaymentDescription(),
                        en.getUpdatedAt().orElse(LocalDateTime.now())
                ));
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public Optional<PaymentMethod> findByName(PaymentMethodName name) {
        Objects.requireNonNull(name, "PaymentMethodName is required");
        
        return paymentMethodRepository.findByPaymentName(name.getValue())
                .map(en -> new PaymentMethod(
                        PaymentMethodId.of(en.getId()),
                        PaymentMethodName.of(en.getPaymentName()),
                        en.getPaymentDescription(),
                        en.getUpdatedAt().orElse(LocalDateTime.now())
                ));
    }

    /**
     * @return 
     */
    @Override
    public List<PaymentMethod> findAll() {
        return paymentMethodRepository.findAll()
                .stream()
                .map(en -> new PaymentMethod(
                        PaymentMethodId.of(en.getId()),
                        PaymentMethodName.of(en.getPaymentName()),
                        en.getPaymentDescription(),
                        en.getUpdatedAt().orElse(LocalDateTime.now())
                ))
                .toList();
    }

    /**
     * @param paymentMethod 
     */
    @Override
    public void delete(PaymentMethod paymentMethod) {
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public boolean existsByName(PaymentMethodName name) {
        Objects.requireNonNull(name, "PaymentMethodName is required");
        
        return paymentMethodRepository.existsByPaymentName(name.getValue());
    }
}
