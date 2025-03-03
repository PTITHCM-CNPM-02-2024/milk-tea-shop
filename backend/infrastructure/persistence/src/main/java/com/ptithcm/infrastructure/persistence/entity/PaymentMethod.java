package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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
public class PaymentMethod extends BaseEntity<Short> {
    @Id
    @Comment("Mã phương thức thanh toán")
    @Column(name = "payment_method_id", columnDefinition = "tinyint UNSIGNED not null")
    private Short id;

    @Comment("Tên phương thức thanh toán")
    @Column(name = "payment_name", nullable = false, length = 50)
    private String paymentName;

    @Comment("Mô tả phương thức thanh toán")
    @Column(name = "payment_description")
    private String paymentDescription;

}