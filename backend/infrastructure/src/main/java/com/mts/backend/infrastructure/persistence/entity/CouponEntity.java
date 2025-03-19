package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponEntity extends BaseEntity<Long> {
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

}