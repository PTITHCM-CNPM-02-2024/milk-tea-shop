package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "Coupon", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "coupon", columnNames = {"coupon"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Coupon extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã coupon")
    @Column(name = "coupon_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @Comment("Mã giảm giá")
    @Column(name = "coupon", nullable = false, length = 15)
    private String coupon;

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    private String description;

    @Comment("Trạng thái (1: Hoạt động, 0: Không hoạt động)")
    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

}