package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "PaymentMethod", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "PaymentMethod_pk", columnNames = {"payment_name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentMethodEntity extends BaseEntity<Integer> {
    @Id
    @Comment("Mã phương thức thanh toán")
    @Column(name = "payment_method_id", columnDefinition = "tinyint UNSIGNED")
    private Integer id;

    @Comment("Tên phương thức thanh toán")
    @Column(name = "payment_name", nullable = false, length = 50)
    private String paymentName;

    @Comment("Mô tả phương thức thanh toán")
    @Column(name = "payment_description")
    private String paymentDescription;

}