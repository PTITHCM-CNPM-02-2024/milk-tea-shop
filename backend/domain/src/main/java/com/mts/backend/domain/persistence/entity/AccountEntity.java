package com.mts.backend.domain.persistence.entity;

import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;

import static java.sql.Types.TIMESTAMP;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã tài khoản")
    @Column(name = "account_id", columnDefinition = "int unsigned")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã vai trò")
    @JoinColumn(name = "role_id")
    private RoleEntity roleEntity;

    @Comment("Tên đăng nhập")
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Comment("Mật khẩu đã mã hóa")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Comment("Tài khoản hoạt động (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

    @Comment("Lần đăng nhập cuối")
    @Column(name = "last_login")
    @JdbcTypeCode(TIMESTAMP)
    private Instant lastLogin;


    @Comment("Tài khoản có bị khóa hay không (Có: 1, Không:0)")
    @ColumnDefault("0")
    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;

    @Comment("Kiểm tra tính hợp lệ của token")
    @ColumnDefault("'0'")
    @Column(name = "token_version", columnDefinition = "int UNSIGNED")
    private Long tokenVersion;

}