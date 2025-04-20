package com.mts.backend.domain.account;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Optional;

import static java.sql.Types.TIMESTAMP;

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
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity<Long> {
    @Id
    @Comment("Mã tài khoản")
    @Column(name = "account_id", columnDefinition = "int unsigned")
    @NotNull
    @Getter
    private Long id;
    
    public static AccountBuilder builder() {
        return new AccountBuilder();
    }

    private boolean setId(@NotNull AccountId id) {
        if (AccountId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @Comment("Mã vai trò")
    @JoinColumn(name = "role_id")
    @NotNull
    @Getter
    private Role role;
    
    public boolean setRole(@NotNull Role role) {
        if (this.role.equals(role)) {
            return false;
        }
        this.role = role;
        return true;
    }

    @Comment("Tên đăng nhập")
    @Column(name = "username", nullable = false, length = 50)
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{3,50}$",
            message = "Tên đăng nhập phải chứa ít nhất 3 ký tự và không quá 50 ký tự")
    private String username;
    
    public Username getUsername() {
        return Username.of(username);
    }
    
    public PasswordHash getPassword() {
        return PasswordHash.of(passwordHash);
    }

    @Comment("Mật khẩu đã mã hóa")
    @Column(name = "password_hash", nullable = false, length = 255)
    @NotNull
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(max = 255, message = "Mật khẩu không được quá 255 ký tự")
    private String passwordHash;

    public PasswordHash getPasswordHash() {
        return PasswordHash.of(passwordHash);
    }
    

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
    @Getter
    @Setter
    private Boolean locked;

    @Comment("Kiểm tra tính hợp lệ của token")
    @ColumnDefault("'0'")
    @Column(name = "token_version", columnDefinition = "int UNSIGNED")
    @Getter
    @Setter
    private Long tokenVersion;


    public boolean setUsername(@NotNull Username userName) {
        if (Username.of(this.username).equals(userName)) {
            return false;
        }
        this.username = userName.getValue();
        return true;
    }

    public boolean setPassword(@NotNull PasswordHash passwordHash) {
        if (PasswordHash.of(this.passwordHash).equals(passwordHash)) {
            return false;
        }
        this.passwordHash = passwordHash.getValue();
        return true;
    }

    public void login() {

        if (this.getActive().isPresent() && this.getActive().get()) {
            return;
        }

        if (this.getLocked()) {
            throw new DomainException("Tài khoản đã bị khóa không thể đăng nhập");
        }
        this.lastLogin = Instant.now();
        this.active = true;
    }

    public void logout() {

        if (this.getActive().isPresent() && !this.getActive().get()) {
            throw new DomainException("Tài khoản đã đăng xuất");
        }

        if (this.getLocked()) {
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

    public boolean setLock(boolean locked) {

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

    public static class AccountBuilder {
        private @NotNull Long id;
        private @NotNull Role role;
        private @NotNull
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]{3,50}$",
                message = "Tên đăng nhập phải chứa ít nhất 3 ký tự và không quá 50 ký tự") String username;
        private @NotNull
        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(max = 255, message = "Mật khẩu không được quá 255 ký tự") String passwordHash;
        private Boolean active;
        private Instant lastLogin;
        private @NotNull Boolean locked;
        private Long tokenVersion;

        AccountBuilder() {
        }

        public AccountBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public AccountBuilder roleEntity(@NotNull Role role) {
            this.role = role;
            return this;
        }

        public AccountBuilder username(@NotNull Username username) {
            this.username = username.getValue();
            return this;
        }

        public AccountBuilder passwordHash(@NotNull PasswordHash passwordHash) {
            this.passwordHash = passwordHash.getValue();
            return this;
        }

        public AccountBuilder active(@Nullable Boolean active) {
            this.active = active;
            return this;
        }

        public AccountBuilder lastLogin(@Nullable Instant lastLogin) {
            this.lastLogin = lastLogin;
            return this;
        }

        public AccountBuilder locked(@NotNull Boolean locked) {
            this.locked = locked;
            return this;
        }

        public AccountBuilder tokenVersion(Long tokenVersion) {
            this.tokenVersion = tokenVersion;
            return this;
        }

        public Account build() {
            return new Account(this.id, this.role, this.username, this.passwordHash, this.active, this.lastLogin, this.locked, this.tokenVersion);
        }

        public String toString() {
            return "Account.AccountBuilder(id=" + this.id + ", role=" + this.role + ", username=" + this.username + ", passwordHash=" + this.passwordHash + ", active=" + this.active + ", lastLogin=" + this.lastLogin + ", locked=" + this.locked + ", tokenVersion=" + this.tokenVersion + ")";
        }
    }
}