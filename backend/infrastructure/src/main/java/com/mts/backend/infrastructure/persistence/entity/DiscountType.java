package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "DiscountType", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "DiscountType_pk", columnNames = {"promotion_name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class DiscountType extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã loại giảm giá")
    @Column(name = "discount_type_id", columnDefinition = "smallint UNSIGNED")
    private Integer id;

    @Comment("Tên loại giảm giá")
    @Column(name = "promotion_name", nullable = false, length = 100)
    private String promotionName;

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    private String description;

}