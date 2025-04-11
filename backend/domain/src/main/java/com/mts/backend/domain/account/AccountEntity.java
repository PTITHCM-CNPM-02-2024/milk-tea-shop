package com.mts.backend.domain.account;

import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static java.sql.Types.TIMESTAMP;

@Getter
@Setter
@Entity
@Table(name = "account", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "role_id", columnList = "role_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "username", columnNames = {"username"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
public class AccountEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã tài khoản")
    @Column(name = "account_id", columnDefinition = "int unsigned")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @Comment("Mã vai trò")
    @JoinColumn(name = "role_id")
    @NotNull
    private RoleEntity roleEntity;

    @Comment("Tên đăng nhập")
    @Column(name = "username", nullable = false, length = 50)
    @Convert(converter = Username.UsernameConverter.class)
    @NotNull
    private Username username;

    @Comment("Mật khẩu đã mã hóa")
    @Column(name = "password_hash", nullable = false, length = 255)
    @Convert(converter = PasswordHash.PasswordHashConverter.class)
    @NotNull
    private PasswordHash passwordHash;

    @Comment("Tài khoản hoạt động (1: Có, 0: Không)")
    @ColumnDefault("0")
    @Column(name = "is_active")
    @Nullable
    private Boolean active;
    
    public Optional<Boolean> getActive() {
        return Optional.ofNullable(active);
    }

    @Comment("Lần đăng nhập cuối")
    @Column(name = "last_login")
    @JdbcTypeCode(TIMESTAMP)
    @Nullable
    private Instant lastLogin;
    
    public Optional<Instant> getLastLogin() {
        return Optional.ofNullable(lastLogin);
    }


    @Comment("Tài khoản có bị khóa hay không (Có: 1, Không:0)")
    @ColumnDefault("0")
    @Column(name = "is_locked", nullable = false)
    @NotNull
    private Boolean locked;
    
    @Comment("Kiểm tra tính hợp lệ của token")
    @ColumnDefault("'0'")
    @Column(name = "token_version", columnDefinition = "int UNSIGNED")
    private Long tokenVersion;

    public AccountEntity(Long id, @NotNull RoleEntity roleEntity, @NotNull Username username, @NotNull PasswordHash passwordHash, @Nullable Boolean active, @Nullable Instant lastLogin, @NotNull Boolean locked, Long tokenVersion) {
        this.id = id;
        this.roleEntity = roleEntity;
        this.username = username;
        this.passwordHash = passwordHash;
        this.active = active;
        this.lastLogin = lastLogin;
        this.locked = locked;
        this.tokenVersion = tokenVersion;
    }

    public AccountEntity() {
    }
    
    public boolean changeUserName(Username userName) {
        if (this.username.equals(userName)) {
            return false;
        }
        this.username = userName;
        return true;
    }
    
    public boolean changePassword(PasswordHash passwordHash) {
        if (this.passwordHash.equals(passwordHash)) {
            return false;
        }
        this.passwordHash = passwordHash;
        return true;
    }
    
    public void login(){
        
        if (this.getActive().isPresent() && this.getActive().get()) {
            throw new DomainException("Tài khoản đã đăng nhập");
        }
        if (this.getLocked()){
            throw new DomainException("Tài khoản đã bị khóa không thể đăng nhập");
        }
        this.lastLogin = Instant.now();
        this.active = true;
    }
    
    public void logout() {
        
        if (this.getActive().isPresent() && !this.getActive().get()) {
            throw new DomainException("Tài khoản đã đăng xuất");
        }
        
        if (this.getLocked()){
            throw new DomainException("Tài khoản đã bị khóa không thể đăng xuất");
        }
        
        this.incrementTokenVersion();
        this.active = false;
    }
    
    public void lock() {
        this.active = false;
        this.incrementTokenVersion();
        this.locked = true;
    }
    
    public void unlock() {
        this.active = null;
        this.incrementTokenVersion();
        this.locked = false;
    }
    
    public boolean changeLock(boolean locked) {
        if (this.locked.equals(locked)) {
            return false;
        }

        if (locked) {
            lock();
        } else {
            unlock();
        }

        return true;
    }

    @Transient
    public Long incrementTokenVersion() {
        if (this.tokenVersion == null) {
            this.tokenVersion = 0L;
        }
        this.tokenVersion++;
        return this.tokenVersion;
    }
}