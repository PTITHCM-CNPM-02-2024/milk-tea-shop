package com.mts.backend.domain.persistence.entity;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.promotion.DiscountEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "OrderDiscount", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDiscountEntity extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã giảm giá đơn hàng")
    @Column(name = "order_discount_id", columnDefinition = "int UNSIGNED")
    private Long id;
    
    @ManyToOne
    @Comment("Mã đơn  hàng áp dụng giảm giá")
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;
    
    @ManyToOne
    @Comment("Mã chương trình giảm giá được áp dụng")
    @JoinColumn(name = "discount_id", nullable = false)
    private DiscountEntity discount;

    
    @Comment("Số tiền giảm giá được áp dụng")
    @Column(name = "discount_amount", nullable = false, precision = 11, scale = 3)
    private BigDecimal discountAmount;

}