package com.mts.backend.domain.account;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

public class Account extends AbstractAggregateRoot<AccountId> {
    @NonNull
    private Username userName;
    @NonNull
    private PasswordHash passwordHash;
    @Nullable
    private Instant lastLogin;
    @NonNull
    private RoleId roleId;
    private boolean isActive;
    private boolean isLocked;
    @NonNull
    private Long tokenVersion;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    
    
    public Account(@NonNull AccountId accountId, @NonNull Username userName, @NonNull PasswordHash passwordHash, @NonNull RoleId roleId, @Nullable Instant lastLogin, Long tokenVersion, boolean isActive, boolean isLocked, @Nullable LocalDateTime updatedAt) {
        super(accountId);
        
        Objects.requireNonNull(userName, "Username is required");
        Objects.requireNonNull(passwordHash, "Password hash is required");
        Objects.requireNonNull(updatedAt, "Updated at is required");
        Objects.requireNonNull(roleId, "Role id is required");
        Objects.requireNonNull(tokenVersion, "Token version is required");
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
        this.roleId = roleId;
        this.isLocked = isLocked;
        this.tokenVersion = tokenVersion;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
    
    public Username getUsername() {
        return userName;
    }
    
    public PasswordHash getPasswordHash() {
        return passwordHash;
    }
    
    public Instant getLastLogin() {
        return lastLogin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean changeUserName(Username userName) {
        Objects.requireNonNull(userName, "Username is required");
        
        if (this.userName.equals(userName)) {
            return false;
        }
        
        this.userName = userName;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changePassword(PasswordHash passwordHash) {
        Objects.requireNonNull(passwordHash, "Password hash is required");
        
        if (this.passwordHash.equals(passwordHash)) {
            return false;
        }
        
        this.passwordHash = passwordHash;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public void login() {
        this.lastLogin = Instant.now();
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void logout() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean changeRole(RoleId roleId) {
        Objects.requireNonNull(roleId, "Role id is required");
        
        if (this.roleId.equals(roleId)) {
            return false;
        }
        
        this.roleId = roleId;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeActive(boolean isActive) {
        if (this.isActive == isActive) {
            return false;
        }
        
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public RoleId getRoleId() {
        return roleId;
    }
    
    public boolean isLocked() {
        return isLocked;
    }
    
    public boolean changeLocked(boolean isLocked) {
        if (this.isLocked == isLocked) {
            return false;
        }
        
        this.isLocked = isLocked;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public void lock() {
        this.isLocked = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void unlock() {
        this.isLocked = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getTokenVersion() {
        return tokenVersion;
    }
    
    public Long incrementTokenVersion() {
        this.tokenVersion++;
        this.updatedAt = LocalDateTime.now();
        return this.tokenVersion;
    }
}
