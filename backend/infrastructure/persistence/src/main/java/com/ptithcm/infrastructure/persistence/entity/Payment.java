package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Payment", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "payment_method_id", columnList = "payment_method_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "order_id", columnNames = {"order_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Payment extends BaseEntity <Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã thanh toán")
    @Column(name = "payment_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Comment("Mã đơn hàng")
    @JoinColumn(name = "order_id")
    private Order order;

    @Comment("Số tiền đã trả")
    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Comment("Tiền thừa")
    @ColumnDefault("0.00")
    @Column(name = "change_amount", precision = 10, scale = 2)
    private BigDecimal changeAmount;

    @Comment("Thời gian thanh toán")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "payment_time")
    private Instant paymentTime;

}