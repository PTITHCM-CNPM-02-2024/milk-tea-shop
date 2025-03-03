package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Account", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "role_id", columnList = "role_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "username", columnNames = {"username"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Account extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã tài khoản")
    @Column(name = "account_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã vai trò")
    @JoinColumn(name = "role_id")
    private com.ptithcm.infrastructure.persistence.entity.Role role;

    @Comment("Tên đăng nhập")
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Comment("Mật khẩu đã mã hóa")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Comment("Tài khoản hoạt động (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

    @Comment("Lần đăng nhập cuối")
    @Column(name = "last_login")
    private Instant lastLogin;
    

}