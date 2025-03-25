package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "OrderTable", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "order_id", columnList = "order_id"),
        @Index(name = "table_id", columnList = "table_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "OrderTable_pk", columnNames = {"order_id", "table_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTableEntity extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã đơn hàng và bàn")
    @Column(name = "order_table_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã đơn hàng")
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;

    @Comment("Thời gian vào bàn")
    @Column(name = "check_in", nullable = false)
    private LocalDateTime checkIn;

    @Comment("Thời gian rời bàn")
    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã bàn")
    @JoinColumn(name = "table_id", nullable = false)
    private ServiceTableEntity table;

}