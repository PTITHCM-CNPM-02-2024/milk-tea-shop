package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "CategoryDiscount", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "discount_id", columnList = "discount_id"),
        @Index(name = "category_id", columnList = "category_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "CategoryDiscount_pk", columnNames = {"discount_id", "category_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class CategoryDiscount extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã định danh duy nhất cho mối quan hệ giữa danh mục và chương trình giảm giá")
    @Column(name = "category_discount_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Liên kết với bảng Discount, xác định chương trình giảm giá áp dụng")
    @JoinColumn(name = "discount_id", nullable = false)
    private com.ptithcm.infrastructure.persistence.entity.Discount discount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Liên kết với bảng Category, xác định danh mục được giảm giá")
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Comment("Số lượng sản phẩm tối thiểu từ danh mục này để áp dụng giảm giá")
    @Column(name = "min_prod_quantity", columnDefinition = "tinyint UNSIGNED not null")
    private Short minProdQuantity;

}