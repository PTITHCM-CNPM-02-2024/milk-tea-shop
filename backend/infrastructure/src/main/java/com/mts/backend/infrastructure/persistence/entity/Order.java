package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
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
@Table(name = "Order", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "employee_id", columnList = "employee_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Order extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã đơn hàng")
    @Column(name = "order_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã khách hàng")
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã nhân viên")
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employeeEntity;

    @Comment("Thời gian đặt hàng")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "order_time")
    private Instant orderTime;

    @Comment("Tổng tiền")
    @Column(name = "total_amount", nullable = false, precision = 11, scale = 3)
    private BigDecimal totalAmount;

    @Comment("Thành tiền")
    @Column(name = "final_amount", nullable = false, precision = 11, scale = 3)
    private BigDecimal finalAmount;

    @Comment("Ghi chú tùy chỉnh")
    @Column(name = "customize_note", length = 1000)
    private String customizeNote;

}