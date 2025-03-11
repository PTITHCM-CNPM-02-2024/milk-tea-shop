package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "Store", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Store extends BaseEntity <Short> {
    @Id
    @Comment("Mã cửa hàng")
    @Column(name = "store_id", columnDefinition = "tinyint UNSIGNED")
    private Short id;

    @Comment("Tên cửa hàng")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Comment("Địa chỉ")
    @Column(name = "address", nullable = false)
    private String address;

    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Comment("Email")
    @Column(name = "email", length = 100)
    private String email;

    @Comment("Ngày khai trương")
    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    @Comment("Mã số thuế")
    @Column(name = "tax_code", length = 20)
    private String taxCode;

}