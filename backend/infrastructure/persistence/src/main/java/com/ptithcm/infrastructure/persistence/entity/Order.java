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
@Table(name = "`Order`", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "employee_id", columnList = "employee_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "Order_pk", columnNames = {"payment_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã đơn hàng")
    @Column(name = "order_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã khách hàng")
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã nhân viên")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne(fetch = FetchType.LAZY)
    @Comment("Mã thanh toán")
    @JoinColumn(name = "payment_id")
    private com.ptithcm.infrastructure.persistence.entity.Payment payment;

    @Comment("Thời gian đặt hàng")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "order_time")
    private Instant orderTime;

    @Comment("Tổng tiền")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Comment("Số tiền giảm giá")
    @ColumnDefault("0.00")
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Comment("Thành tiền")
    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Comment("Ghi chú tùy chỉnh")
    @Lob
    @Column(name = "customize_note")
    private String customizeNote;

}