package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.domain.common.value_object.Gender;
import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "Manager", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "account_id", columnNames = {"account_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerEntity extends BaseEntity <Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã quản lý")
    @Column(name = "manager_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Comment("Mã tài khoản")
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;

    @Comment("Họ")
    @Column(name = "last_name", nullable = false, length = 35)
    private String lastName;

    @Comment("Tên")
    @Column(name = "first_name", nullable = false, length = 25)
    private String firstName;

    @Comment("Giới tính")
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Comment("Email")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

}