package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "Customer", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "membership_type_id", columnList = "membership_type_id"),
        @Index(name = "account_id", columnList = "account_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "phone", columnNames = {"phone"}),
        @UniqueConstraint(name = "email", columnNames = {"email"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Customer extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã khách hàng")
    @Column(name = "customer_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã loại thành viên")
    @JoinColumn(name = "membership_type_id")
    private com.ptithcm.infrastructure.persistence.entity.MembershipType membershipType;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã tài khoản")
    @JoinColumn(name = "account_id")
    private Account account;

    @Comment("Họ")
    @Column(name = "last_name", nullable = false, length = 35)
    private String lastName;

    @Comment("Tên")
    @Column(name = "first_name", nullable = false, length = 25)
    private String firstName;

    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Comment("Email")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Comment("Điểm hiện tại")
    @ColumnDefault("0")
    @Column(name = "current_points")
    private Integer currentPoints;

    @Comment("Giới tính")
    @Lob
    @Column(name = "sex")
    private String sex;

}