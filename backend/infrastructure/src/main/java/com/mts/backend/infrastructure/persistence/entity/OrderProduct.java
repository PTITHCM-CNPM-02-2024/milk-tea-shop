package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "OrderProduct", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "order_id", columnList = "order_id"),
        @Index(name = "product_price_id", columnList = "product_price_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "OrderProduct_pk", columnNames = {"order_id", "product_price_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class OrderProduct extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã chi tiết đơn hàng")
    @Column(name = "order_product_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã đơn hàng")
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;

    @Comment("Số lượng")
    @Column(name = "quantity", columnDefinition = "smallint UNSIGNED", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã giá sản phẩm")
    @JoinColumn(name = "product_price_id", nullable = false)
    private ProductPriceEntity productPriceEntity;

    @Comment("Tùy chọn cho việc lựa chọn lượng đá, đường")
    @Column(name = "`option`", length = 500)
    private String option;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã đặt hàng sản phẩm gốc, khi sản phẩm ở hàng này được đặt là Topping")
    @JoinColumn(name = "parent_order_product_id")
    private OrderProduct parentOrderProduct;

}