package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "Supplier", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Supplier extends BaseEntity <Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã nhà cung cấp")
    @Column(name = "supplier_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @Comment("Tên nhà cung cấp")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Comment("Người liên hệ")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Comment("Email")
    @Column(name = "email", length = 100)
    private String email;

    @Comment("Địa chỉ")
    @Lob
    @Column(name = "address")
    private String address;

}