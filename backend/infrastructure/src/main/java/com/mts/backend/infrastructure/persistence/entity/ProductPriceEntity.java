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
@Table(name = "ProductPrice", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "product_id", columnList = "product_id"),
        @Index(name = "size_id", columnList = "size_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "ProductPrice_pk", columnNames = {"product_id", "size_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class ProductPriceEntity extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã giá sản phẩm")
    @Column(name = "product_price_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã sản phẩm")
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @Comment("Giá")
    @Column(name = "price", nullable = false, precision = 11, scale = 3)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã kích thước")
    @JoinColumn(name = "size_id")
    private ProductSizeEntity size;

}