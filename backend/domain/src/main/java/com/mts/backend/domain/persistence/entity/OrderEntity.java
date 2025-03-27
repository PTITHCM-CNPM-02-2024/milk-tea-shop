package com.mts.backend.domain.persistence.entity;

import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "`Order`", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "employee_id", columnList = "employee_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NamedEntityGraphs(
        @NamedEntityGraph(name = "OrderEntity.detail",
                attributeNodes = {
                        @NamedAttributeNode("orderDiscounts"),
                        @NamedAttributeNode("orderProducts"),
                        @NamedAttributeNode("orderTables")
                }
        )
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã đơn hàng")
    @Column(name = "order_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @ManyToOne
    @Comment("Mã khách hàng")
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;

    @ManyToOne
    @Comment("Mã nhân viên")
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employeeEntity;

    @Comment("Thời gian đặt hàng")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "order_time", nullable = false)
    private Instant orderTime;

    @Comment("Tổng tiền")
    @Column(name = "total_amount", precision = 11, scale = 3)
    private BigDecimal totalAmount;

    @Comment("Thành tiền")
    @Column(name = "final_amount", precision = 11, scale = 3)
    private BigDecimal finalAmount;

    @Comment("Ghi chú tùy chỉnh")
    @Column(name = "customize_note", length = 1000)
    private String customizeNote;

    @Comment("Trạng thái đơn hàng")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @OneToMany(mappedBy = "orderEntity", fetch = FetchType.LAZY)
    private Set<OrderDiscountEntity> orderDiscounts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "orderEntity", fetch = FetchType.LAZY)
    private Set<OrderProductEntity> orderProducts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "orderEntity", fetch = FetchType.LAZY)
    private Set<OrderTableEntity> orderTables = new LinkedHashSet<>();

}