package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "OrderDiscount", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "OrderDiscount_pk", columnNames = {"order_product_id", "discount_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class OrderDiscount extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã giảm giá đơn hàng")
    @Column(name = "order_discount_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã chương trình giảm giá được áp dụng")
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    @Comment("Số tiền giảm giá được áp dụng")
    @Column(name = "discount_amount", nullable = false, precision = 11, scale = 3)
    private BigDecimal discountAmount;

}