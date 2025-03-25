package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Payment", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "payment_method_id", columnList = "payment_method_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity extends BaseEntity <Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã thanh toán")
    @Column(name = "payment_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @Comment("Số tiền đã trả")
    @Column(name = "amount_paid", nullable = false, precision = 11, scale = 3)
    private BigDecimal amountPaid;

    @Comment("Tiền thừa")
    @ColumnDefault("0.00")
    @Column(name = "change_amount", precision = 11, scale = 3)
    private BigDecimal changeAmount;

    @Comment("Thời gian thanh toán")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "payment_time")
    private Instant paymentTime;

    @ManyToOne
    @Comment("Mã phương thức thanh toán")
    @JoinColumn(name = "payment_method_id")
    private PaymentMethodEntity paymentMethod;

    @ManyToOne
    @Comment("Mã đơn hàng")
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;

    @Comment("Trạng thái thanh toán")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

}