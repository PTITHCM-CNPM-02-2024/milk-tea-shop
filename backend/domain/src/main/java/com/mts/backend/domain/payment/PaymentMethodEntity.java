package com.mts.backend.domain.payment;

import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.lang.Nullable;

import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "PaymentMethod", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "PaymentMethod_pk", columnNames = {"payment_name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentMethodEntity extends BaseEntity<Integer> {
    @Id
    @Comment("Mã phương thức thanh toán")
    @Column(name = "payment_method_id", columnDefinition = "tinyint UNSIGNED")
    @NotNull
    private Integer id;

    @Comment("Tên phương thức thanh toán")
    @Column(name = "payment_name", nullable = false, length = 50)
    @Convert(converter = PaymentMethodName.PaymentMethodNameConverter.class)
    @NonNull
    private PaymentMethodName paymentName;

    @Comment("Mô tả phương thức thanh toán")
    @Column(name = "payment_description")
    @Nullable
    private String paymentDescription;
    
    public Optional<String> getPaymentDescription() {
        return Optional.ofNullable(paymentDescription);
    }

    public boolean changeName(PaymentMethodName name) {
        if (paymentName.equals(name)) {
            return false;
        }
        this.paymentName = name;
        return true;
    }

}