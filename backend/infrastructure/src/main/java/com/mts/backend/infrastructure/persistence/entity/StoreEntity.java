package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "Store", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEntity extends BaseEntity <Integer> {
    @Id
    @Comment("Mã cửa hàng")
    @Column(name = "store_id", columnDefinition = "tinyint UNSIGNED")
    private Integer id;

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

    @Comment("Thời gian mở cửa")
    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

}