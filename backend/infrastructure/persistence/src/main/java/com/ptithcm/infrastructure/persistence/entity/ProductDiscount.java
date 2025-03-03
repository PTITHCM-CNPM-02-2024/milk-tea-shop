package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "ProductDiscount", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "discount_id", columnList = "discount_id"),
        @Index(name = "product_price_id", columnList = "product_price_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "ProductDiscount_pk", columnNames = {"discount_id", "product_price_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class ProductDiscount extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã định danh duy nhất cho mối quan hệ giữa sản phẩm và chương trình giảm giá")
    @Column(name = "product_discount_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Liên kết với bảng Discount, xác định chương trình giảm giá áp dụng")
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    @Comment("Số lượng tối thiểu của sản phẩm để áp dụng giảm giá")
    @Column(name = "min_prod_quantity", columnDefinition = "tinyint UNSIGNED not null")
    private Short minProdQuantity;

}